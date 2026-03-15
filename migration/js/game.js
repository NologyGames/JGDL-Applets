const STATES = {
  MAIN: 'main',
  PREGAME: 'pregame',
  GAME: 'game',
  LEVELUP: 'levelup',
  GAMEOVER: 'gameover',
  INITLEVEL: 'initlevel',
  CONGRATS: 'congrats',
};

function createPopcornGame(canvas, ui) {
  const ctx = canvas.getContext('2d');
  const width = CONST.CANVAS_WIDTH;
  const height = CONST.CANVAS_HEIGHT;

  let state = STATES.MAIN;
  let currentLevel = 0;
  let points = 0;
  let foundCorns = 0;
  let cornsToFind = 150;
  let newLineTimer = 0;
  let newLineLimit = 6500;
  let loadLinesTimer = 0;
  let loadLinesLimit = 300;
  let loadLinesCount = 3;
  let flexibility = 1;
  let lastPopSnd = 0;
  let paused = false;
  let boards = [];
  let freePieces = [];
  let effects;
  let levelConfig;
  let assets = null;
  let levelRef = null;

  const CORN_IMGS = ['inp_RedCorn.gif', 'inp_DarkGreenCorn.gif', 'inp_BlueCorn.gif', 'inp_YellowCorn.gif'];

  var SPM_ANIM = typeof window !== 'undefined' && window.SPM_ANIM ? window.SPM_ANIM : {};
  var flameStates = [];
  var shakeTime = 0;
  for (var fi = 0; fi < 18; fi++) {
    flameStates.push({ animIndex: fi < 9 ? 0 : 0, time: (fi * 37) % 300 });
  }

  function rand() {
    return Math.floor(Math.random() * 0x7fffffff);
  }

  function getLevelConfig() {
    const c = CONST.LEVELS[currentLevel];
    if (!c) return CONST.LEVELS[CONST.LEVELS.length - 1];
    return c;
  }

  function getRandomPiece(boardIndex) {
    const id = (rand() % 4) + 1;
    const b = boards[boardIndex];
    if (!levelRef) return null;
    let p = freePieces.find(px => px.id === id && px.powerUpType === 0);
    if (p) {
      freePieces = freePieces.filter(px => px !== p);
      p.board = b;
      p.level = levelRef;
      p.x = p.y = 0;
      p.falling = true;
      p.effect = WPE_NONE;
      p.visited = false;
      return p;
    }
    p = createPiece(id, b, levelRef);
    return p;
  }

  function syncLevelRef() {
    if (!levelRef) return;
    levelRef.state = state;
    levelRef.points = points;
    levelRef.foundCorns = foundCorns;
    levelRef.freePieces = freePieces;
    levelRef.rand = rand;
  }

  function initLevel() {
    levelConfig = getLevelConfig();
    cornsToFind = levelConfig.cornsToFind;
    newLineLimit = levelConfig.newLineMs;
    newLineTimer = 0;
    loadLinesLimit = 300;
    loadLinesCount = 3;
    loadLinesTimer = 0;
    flexibility = levelConfig.flexibility + currentLevel * CONST.FLEX_PER_LEVEL;
    foundCorns = 0;

    boards[0].setPos(CONST.BOARD1_X, CONST.BOARD_Y);
    boards[1].setPos(CONST.BOARD2_X, CONST.BOARD_Y);
    boards[0].clear();
    boards[1].clear();
    state = STATES.PREGAME;
  }

  function updateBalance(dt) {
    const n0 = boards[0].countPieces();
    const n1 = boards[1].countPieces();
    let speed = (n0 - n1) * flexibility;
    speed = Math.max(-CONST.WSPEED_LIMIT, Math.min(CONST.WSPEED_LIMIT, speed));
    if (speed === 0) {
      const py0 = boards[0].posY();
      const py1 = boards[1].posY();
      if (Math.abs(py0 - py1) < 1) speed = 0;
      else speed = py1 > py0 ? 5 : -5;
    }
    const mult = speed * dt;
    const maxY = height - 187;
    boards[0].setPos(boards[0].posX(), Math.min(maxY, Math.max(0, boards[0].posY() + mult)));
    boards[1].setPos(boards[1].posX(), Math.min(maxY, Math.max(0, boards[1].posY() - mult)));
  }

  function hitTestBoard(boardIndex, mx, my) {
    const b = boards[boardIndex];
    const px = b.posX();
    const py = b.posY();
    const left = px;
    const right = px + MAT_X * PIECE_SIZE;
    const top = py;
    const bottom = py + MAT_Y * PIECE_SIZE;
    if (mx < left || mx > right || my < top || my > bottom) return null;
    const col = Math.floor((mx - left) / PIECE_SIZE);
    const row = Math.floor((my - top) / PIECE_SIZE);
    const idx = col + row * MAT_X;
    return b.matrixRef[idx] || null;
  }

  function getImg(name) {
    if (!assets) return null;
    var path = assets.VideoManager ? (assets.ImagesDir + name) : (assets.basePath ? assets.basePath + name : name);
    if (assets.VideoManager) {
      var rec = assets.VideoManager.LoadImage(path);
      return rec && rec.image ? rec.image : null;
    }
    return AssetLoader.getImage(path);
  }

  function drawSpriteFrame(img, frameIndex, cellW, cellH, dx, dy, dw, dh) {
    if (!img || frameIndex < 0) return;
    var cols = Math.max(1, Math.floor(img.width / cellW));
    var sx = (frameIndex % cols) * cellW;
    var sy = Math.floor(frameIndex / cols) * cellH;
    ctx.drawImage(img, sx, sy, cellW, cellH, dx, dy, dw || cellW, dh || cellH);
  }

  function draw() {
    const base = assets ? (assets.ImagesDir || assets.basePath || '') : '';
    if (state === STATES.MAIN) {
      const mainImg = assets && getImg('spr_MainScreen.gif');
      if (mainImg) ctx.drawImage(mainImg, 0, 0, width, height);
      else { ctx.fillStyle = '#3d2914'; ctx.fillRect(0, 0, width, height); }
      effects.draw(getImg);
      return;
    }
    if (assets && getImg('bkg_Movies.gif')) {
      ctx.drawImage(getImg('bkg_Movies.gif'), 0, 0, width, height);
    } else {
      ctx.fillStyle = '#3d2914';
      ctx.fillRect(0, 0, width, height);
      ctx.fillStyle = '#4a3728';
      ctx.fillRect(10, 320, 140, 16);
      ctx.fillRect(298, 320, 140, 16);
    }

    var stoveImg = assets && getImg('spr_Stove.gif');
    if (stoveImg) {
      ctx.drawImage(stoveImg, 0, 0, 140, 16, 10, 320, 140, 16);
      ctx.drawImage(stoveImg, 0, 0, 140, 16, 298, 320, 140, 16);
    }

    var fireImg = assets && getImg('spr_StoveFire.gif');
    if (fireImg && SPM_ANIM.FLAME_POSITIONS) {
      var fw = 14, fh = 21;
      for (var fi = 0; fi < 18; fi++) {
        var st = flameStates[fi];
        var anim = SPM_ANIM.FLAME_ANIMS && SPM_ANIM.FLAME_ANIMS[st.animIndex];
        var frameMs = SPM_ANIM.FLAME_FRAME_MS && SPM_ANIM.FLAME_FRAME_MS[st.animIndex];
        if (anim && frameMs) {
          var idx = Math.floor(st.time / frameMs) % anim.length;
          var f = anim[idx];
          if (f >= 0) {
            var pos = SPM_ANIM.FLAME_POSITIONS[fi];
            drawSpriteFrame(fireImg, f, fw, fh, pos.x, pos.y, fw, fh);
          }
        }
      }
    }

    for (let bi = 0; bi < 2; bi++) {
      const b = boards[bi];
      const px = b.posX();
      const py = b.posY();
      const panImg = assets && getImg('spr_pan.gif');
      if (panImg) {
        ctx.drawImage(panImg, 0, 0, 129, 213, px - 5, py - 20, 129, 213);
      } else {
        ctx.fillStyle = '#5c4033';
        ctx.fillRect(px - 5, py - 20, 129, 213);
        ctx.strokeStyle = '#8b6914';
        ctx.lineWidth = 2;
        ctx.strokeRect(px - 5, py - 20, 129, 213);
      }

      for (let i = 0; i < MAT_SIZE; i++) {
        const p = b.matrixRef[i];
        if (!p || p.effect === WPE_EXPLODE) continue;
        const x = px + p.x;
        const y = py + p.y;
        const imgName = CORN_IMGS[(p.id - 1) % 4];
        const img = assets && getImg(imgName);
        if (img) {
          var wpa = p.animState != null ? p.animState : (p.falling ? SPM_ANIM.WPA_FALLING : SPM_ANIM.WPA_STOPED1);
          var anim = SPM_ANIM.getPieceAnimByIndex && SPM_ANIM.getPieceAnimByIndex(wpa);
          var frameIdx = anim && SPM_ANIM.getPieceFrameIndex ? SPM_ANIM.getPieceFrameIndex(anim, p.animTimer || 0) : 0;
          drawSpriteFrame(img, frameIdx, 17, 17, x, y, 17, 17);
        } else {
          ctx.fillStyle = CONST.COLORS[(p.id - 1) % 4];
          ctx.beginPath();
          ctx.arc(x + 8, y + 8, 6, 0, Math.PI * 2);
          ctx.fill();
        }
      }

      for (let c = 0; c < MAT_X; c++) {
        const np = b.newPiecesRef[c];
        if (np && np.y >= -PIECE_SIZE) {
          const x = px + np.x;
          const y = py + np.y;
          const imgName = CORN_IMGS[(np.id - 1) % 4];
          const img = assets && getImg(imgName);
          if (img) {
            var wpa = np.animState != null ? np.animState : SPM_ANIM.WPA_FALLING;
            var anim = SPM_ANIM.getPieceAnimByIndex && SPM_ANIM.getPieceAnimByIndex(wpa);
            var frameIdx = anim && SPM_ANIM.getPieceFrameIndex ? SPM_ANIM.getPieceFrameIndex(anim, np.animTimer || 0) : 0;
            drawSpriteFrame(img, frameIdx, 17, 17, x, y, 17, 17);
          } else {
            ctx.fillStyle = CONST.COLORS[(np.id - 1) % 4];
            ctx.beginPath();
            ctx.arc(x + 8, y + 8, 6, 0, Math.PI * 2);
            ctx.fill();
          }
        }
      }
    }

    var layInfoImg = assets && getImg('lay_Info.gif');
    if (layInfoImg) ctx.drawImage(layInfoImg, 0, 0, 127, 166, 161, 0, 127, 166);

    var sensorBackImg = assets && getImg('lay_sensorback.gif');
    var sensorPointImg = assets && getImg('inp_SensorDisplay.gif');
    if (sensorBackImg && sensorPointImg && boards[0] && boards[1]) {
      var py0 = boards[0].posY(), py1 = boards[1].posY();
      var iDif = Math.abs(py0 - py1);
      var iMaxDif = 144;
      var iAnims = 25;
      var iAnim = Math.min(24, Math.floor((Math.min(iDif, iMaxDif) * iAnims) / iMaxDif));
      var bSum = py0 > py1;
      var sensBackFrame = iAnim < 5 ? 0 : (iAnim > 15 ? (bSum ? 2 : 3) : 1);
      var sensPointFrame = iAnim + (bSum ? 25 : 0);
      var sbCols = Math.max(1, Math.floor(sensorBackImg.width / 79));
      var sbFx = (sensBackFrame % sbCols) * 79;
      var sbFy = Math.floor(sensBackFrame / sbCols) * 51;
      ctx.drawImage(sensorBackImg, sbFx, sbFy, 79, 51, 184, 105, 79, 51);
      drawSpriteFrame(sensorPointImg, sensPointFrame, 44, 19, 202, 117, 44, 19);
    }

    var popsBarImg = assets && getImg('spr_PopsBar.gif');
    if (popsBarImg && state !== STATES.MAIN) {
      var progress = cornsToFind > 0 ? Math.min(1, foundCorns / cornsToFind) : 0;
      var barW = 80, barH = 16;
      var cols = Math.max(1, Math.floor(popsBarImg.width / barW));
      ctx.drawImage(popsBarImg, (1 % cols) * barW, 0, barW, barH, 184, 60, barW, barH);
      if (progress > 0) {
        var fillW = barW * progress;
        ctx.drawImage(popsBarImg, 0, 0, fillW, barH, 184, 60, fillW, barH);
      }
    }

    var shakeImg = assets && getImg('spr_alavanca.gif');
    if (shakeImg && SPM_ANIM.SHAKE_FRAMES_BOARD1) {
      for (var bi = 0; bi < 2; bi++) {
        var b = boards[bi];
        var px = b.posX(), py = b.posY();
        var frames = bi === 0 ? SPM_ANIM.SHAKE_FRAMES_BOARD1 : SPM_ANIM.SHAKE_FRAMES_BOARD2;
        var frameIdx = frames[Math.floor(shakeTime / (SPM_ANIM.SHAKE_FRAME_MS || 20)) % frames.length];
        drawSpriteFrame(shakeImg, frameIdx, 124, 41, px - 2, py - 62, 124, 41);
      }
    }

    var colorLightsImg = assets && getImg('spr_colorlights.gif');
    if (colorLightsImg) {
      for (var bi = 0; bi < 2; bi++) {
        var b = boards[bi];
        var px = b.posX(), py = b.posY();
        for (var c = 0; c < MAT_X; c++) {
          var np = b.newPiecesRef[c];
          var frame = np ? (np.id - 1) : 5;
          if (frame >= 0 && frame <= 5) drawSpriteFrame(colorLightsImg, frame, 6, 6, px + 5 + c * 17, py - 8, 6, 6);
        }
      }
    }

    for (let bi = 0; bi < 2; bi++) {
      const b = boards[bi];
      const px = b.posX();
      const py = b.posY();
      for (let i = 0; i < MAT_SIZE; i++) {
        const p = b.matrixRef[i];
        if (!p || p.effect !== WPE_EXPLODE) continue;
        const x = px + p.x;
        const y = py + p.y;
        const imgName = CORN_IMGS[(p.id - 1) % 4];
        const img = assets && getImg(imgName);
        if (img) {
          var burnAnim = SPM_ANIM.getPieceAnimByIndex && SPM_ANIM.getPieceAnimByIndex(SPM_ANIM.WPA_BURNING1);
          var frameIdx = burnAnim && SPM_ANIM.getPieceFrameIndex ? SPM_ANIM.getPieceFrameIndex(burnAnim, p.animTimer || 0) : 0;
          drawSpriteFrame(img, frameIdx, 17, 17, x, y, 17, 17);
        }
      }
    }

    effects.draw(getImg);
  }

  function update(dt) {
    if (paused) return;
    if (levelRef) syncLevelRef();

    var dtMs = dt * 1000;
    if (SPM_ANIM.FLAME_FRAME_MS && SPM_ANIM.FLAME_ANIMS) {
      for (var fi = 0; fi < flameStates.length; fi++) {
        var st = flameStates[fi];
        st.time += dtMs;
        var anim = SPM_ANIM.FLAME_ANIMS[st.animIndex];
        var frameMs = SPM_ANIM.FLAME_FRAME_MS[st.animIndex];
        if (anim && frameMs && anim.length) {
          var total = anim.length * frameMs;
          if (st.animIndex === 0 && st.time >= total) {
            st.animIndex = 1;
            st.time = st.time % total;
          } else if (st.animIndex === 1 && total > 0) {
            st.time = st.time % total;
          }
        }
      }
    }
    shakeTime += dtMs;

    var WPA_FALLING = SPM_ANIM.WPA_FALLING != null ? SPM_ANIM.WPA_FALLING : 28;
    var WPA_FALLED1 = SPM_ANIM.WPA_FALLED1 != null ? SPM_ANIM.WPA_FALLED1 : 5;
    var WPA_STOPED1 = SPM_ANIM.WPA_STOPED1 != null ? SPM_ANIM.WPA_STOPED1 : 1;
    var WPA_BURNING1 = SPM_ANIM.WPA_BURNING1 != null ? SPM_ANIM.WPA_BURNING1 : 13;
    for (var bi = 0; bi < 2; bi++) {
      var b = boards[bi];
      for (var i = 0; i < MAT_SIZE; i++) {
        var p = b.matrixRef[i];
        if (p) {
          if (p.animTimer == null) p.animTimer = 0;
          p.animTimer += dtMs;
          if (p.effect === 1) p.animState = WPA_BURNING1;
          else if (p.falling) p.animState = WPA_FALLING;
          else if (p.animState === WPA_FALLING) {
            p.animState = (rand() % 2 === 0) ? WPA_FALLED1 : (SPM_ANIM.WPA_FALLED2 != null ? SPM_ANIM.WPA_FALLED2 : 6);
            p.animTimer = 0;
          } else if ((p.animState === WPA_FALLED1 && p.animTimer >= 2730) || (p.animState === (SPM_ANIM.WPA_FALLED2 != null ? SPM_ANIM.WPA_FALLED2 : 6) && p.animTimer >= 2490)) {
            p.animState = WPA_STOPED1;
            p.animTimer = 0;
          } else if (p.animState == null) p.animState = WPA_STOPED1;
        }
      }
      for (var c = 0; c < MAT_X; c++) {
        var np = b.newPiecesRef[c];
        if (np) {
          if (np.animTimer == null) np.animTimer = 0;
          np.animTimer += dtMs;
          if (np.animState == null) np.animState = WPA_FALLING;
        }
      }
    }

    if (state === STATES.PREGAME) {
      loadLinesTimer += dt * 1000;
      if (loadLinesTimer >= loadLinesLimit) {
        loadLinesTimer = 0;
        if (!boards[0].createLine(() => getRandomPiece(0)) || !boards[1].createLine(() => getRandomPiece(1))) {
          state = STATES.GAMEOVER;
          return;
        }
        loadLinesCount--;
        if (loadLinesCount <= 0) state = STATES.GAME;
        AudioManager.menuMove();
      }
      boards[0].handleFalling(dt);
      boards[1].handleFalling(dt);
      boards[0].updateRemoveList(dt, () => AudioManager.pop(), effects);
      boards[1].updateRemoveList(dt, () => AudioManager.pop(), effects);
      boards[0].checkColumns();
      boards[1].checkColumns();
      points = levelRef.points;
      foundCorns = levelRef.foundCorns;
    } else if (state === STATES.GAME) {
      newLineTimer += dt * 1000;
      if (newLineTimer >= newLineLimit) {
        newLineTimer = 0;
        if (!boards[0].createLine(() => getRandomPiece(0)) || !boards[1].createLine(() => getRandomPiece(1))) {
          state = STATES.GAMEOVER;
          return;
        }
        AudioManager.menuMove();
      }
      updateBalance(dt);
      boards[0].handleFalling(dt);
      boards[1].handleFalling(dt);
      boards[0].updateRemoveList(dt, () => AudioManager.pop(), effects);
      boards[1].updateRemoveList(dt, () => AudioManager.pop(), effects);
      boards[0].checkColumns();
      boards[1].checkColumns();
      points = levelRef.points;
      foundCorns = levelRef.foundCorns;

      if (Math.abs(boards[0].posY() - boards[1].posY()) > CONST.BALANCE_LIMIT) {
        state = STATES.GAMEOVER;
      }
      if (foundCorns >= cornsToFind) {
        state = STATES.LEVELUP;
      }
    } else if (state === STATES.LEVELUP) {
      boards[0].clear();
      boards[1].clear();
      state = currentLevel < CONST.LEVELS.length - 1 ? STATES.INITLEVEL : STATES.CONGRATS;
    }

    effects.update(dt);
  }

  function onClick(mx, my) {
    if (state === STATES.MAIN) {
      state = STATES.PREGAME;
      initLevel();
      ui.hideMenu();
      ui.showHud();
      ui.updateHud(1, foundCorns, cornsToFind, points);
      return;
    }
    if (state === STATES.GAME || state === STATES.PREGAME) {
      for (let bi = 0; bi < 2; bi++) {
        const p = hitTestBoard(bi, mx, my);
        if (p) {
          if (p.powerUpType !== 0) {
            // execute power-up (simplified: just remove and add points)
            freePieces.push(p);
            boards[bi].matrixRef[p.index] = null;
            points += 50;
            return;
          }
          boards[bi].clearVisited();
          boards[bi].deleteRecursive(p.index % MAT_X, Math.floor(p.index / MAT_X));
          return;
        }
      }
    }
  }

  function setLevelComplete() {
    state = currentLevel < CONST.LEVELS.length - 1 ? STATES.INITLEVEL : STATES.CONGRATS;
  }

  function showLevelUp() {
    currentLevel++;
    initLevel();
    state = STATES.PREGAME;
    ui.hidePopup();
    ui.updateHud(currentLevel + 1, foundCorns, cornsToFind, points);
  }

  function showGameOver() {
    points = 0;
    currentLevel = 0;
    initLevel();
    state = STATES.PREGAME;
    ui.hidePopup();
    ui.updateHud(1, foundCorns, cornsToFind, points);
  }

  function showCongrats() {
    currentLevel = 0;
    points = 0;
    initLevel();
    state = STATES.PREGAME;
    ui.hidePopup();
    ui.updateHud(1, foundCorns, cornsToFind, points);
  }

  function init(loadedAssets) {
    assets = loadedAssets || null;
    effects = createEffects(ctx);
    levelRef = { state, freePieces, points, foundCorns, rand };
    boards = [
      createBoard(levelRef, 0),
      createBoard(levelRef, 1),
    ];
    state = STATES.MAIN;
    currentLevel = 0;
    points = 0;
    foundCorns = 0;
    loadLinesCount = 3;
    loadLinesTimer = 0;
    newLineTimer = 0;
  }

  return {
    setAssets(a) { assets = a; },
    init,
    update,
    draw,
    onClick,
    getState: () => state,
    getPoints: () => points,
    getFoundCorns: () => foundCorns,
    getCornsToFind: () => cornsToFind,
    getCurrentLevel: () => currentLevel,
    showLevelUp,
    showGameOver,
    showCongrats,
    setPaused: (v) => { paused = v; },
    isPaused: () => paused,
  };
}
