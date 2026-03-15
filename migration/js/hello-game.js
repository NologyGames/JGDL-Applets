const HELLO_MAT_X = 9, HELLO_MAT_Y = 10, HELLO_MAT_SIZE = 90, HELLO_PIECE = 31;

function createHelloBoard() {
  const matrix = Array(HELLO_MAT_SIZE).fill(null);
  const removeList = [];
  let removeTimer = 0;
  const upLeftX = HELLO.UP_LEFT_X, upLeftY = HELLO.UP_LEFT_Y;

  function index(i, j) {
    return i + j * HELLO_MAT_X;
  }

  function toScreen(mx, my) {
    return {
      x: upLeftX + mx * HELLO_PIECE,
      y: upLeftY + my * HELLO_PIECE,
    };
  }

  function fromScreen(sx, sy) {
    const mx = Math.floor((sx - upLeftX) / HELLO_PIECE);
    const my = Math.floor((sy - upLeftY) / HELLO_PIECE);
    if (mx < 0 || mx >= HELLO_MAT_X || my < 0 || my >= HELLO_MAT_Y) return null;
    return { mx, my, idx: index(mx, my) };
  }

  function clearVisited() {
    for (let i = 0; i < HELLO_MAT_SIZE; i++)
      if (matrix[i]) matrix[i].visited = false;
  }

  /** True if cell has a phone and has at least one empty neighbor (on the "edge"). */
  function canSelect(mx, my) {
    if (mx < 0 || mx >= HELLO_MAT_X || my < 0 || my >= HELLO_MAT_Y) return false;
    const p = matrix[index(mx, my)];
    if (!p) return false;
    if (mx > 0 && !matrix[index(mx - 1, my)]) return true;
    if (my > 0 && !matrix[index(mx, my - 1)]) return true;
    if (mx < HELLO_MAT_X - 1 && !matrix[index(mx + 1, my)]) return true;
    if (my < HELLO_MAT_Y - 1 && !matrix[index(mx, my + 1)]) return true;
    return false;
  }

  /** Nearest selectable cell to (mx, my), or (mx, my) if selectable. */
  function getNearestConnectionPos(mx, my) {
    if (mx < 0 || mx >= HELLO_MAT_X || my < 0 || my >= HELLO_MAT_Y) return null;
    if (!matrix[index(mx, my)] || canSelect(mx, my)) return { mx, my };
    let best = null;
    let bestDist = 1e9;
    for (let dy = -HELLO_MAT_Y; dy <= HELLO_MAT_Y; dy++) {
      for (let dx = -HELLO_MAT_X; dx <= HELLO_MAT_X; dx++) {
        const nx = mx + dx;
        const ny = my + dy;
        if (nx >= 0 && nx < HELLO_MAT_X && ny >= 0 && ny < HELLO_MAT_Y && canSelect(nx, ny)) {
          const d = dx * dx + dy * dy;
          if (d < bestDist) {
            bestDist = d;
            best = { mx: nx, my: ny };
          }
        }
      }
    }
    return best;
  }

  /** BFS path through empty cells from (fx,fy) to (tx,ty). Returns array of {mx,my} or null. */
  function findPath(fx, fy, tx, ty) {
    const start = { mx: fx, my: fy };
    const end = { mx: tx, my: ty };
    const canWalk = (mx, my) => {
      if (mx < 0 || mx >= HELLO_MAT_X || my < 0 || my >= HELLO_MAT_Y) return false;
      if (mx === tx && my === ty) return true;
      return !matrix[index(mx, my)];
    };
    const key = (c) => c.mx + ',' + c.my;
    const queue = [start];
    const parent = { [key(start)]: null };
    while (queue.length) {
      const c = queue.shift();
      if (c.mx === tx && c.my === ty) {
        const path = [];
        for (let p = c; p; p = parent[key(p)]) path.unshift(p);
        return path;
      }
      const dirs = [[0, -1], [0, 1], [-1, 0], [1, 0]];
      for (const [dx, dy] of dirs) {
        const n = { mx: c.mx + dx, my: c.my + dy };
        if (canWalk(n.mx, n.my) && !parent[key(n)]) {
          parent[key(n)] = c;
          queue.push(n);
        }
      }
    }
    return null;
  }

  const WIRE_PREVLEFT = 1, WIRE_PREVRIGHT = 2, WIRE_PREVUP = 3, WIRE_PREVDOWN = 4;
  const WIRE_NEXTLEFT = 8, WIRE_NEXTRIGHT = 16, WIRE_NEXTUP = 32, WIRE_NEXTDOWN = 64;

  let path = [];
  let pathAnim = []; // frame index per path segment (for wire sprite)

  function dirPrev(a, b) {
    if (a.mx < b.mx) return WIRE_PREVLEFT;
    if (a.mx > b.mx) return WIRE_PREVRIGHT;
    if (a.my < b.my) return WIRE_PREVUP;
    return WIRE_PREVDOWN;
  }
  function dirNext(a, b) {
    if (b.mx < a.mx) return WIRE_NEXTLEFT;
    if (b.mx > a.mx) return WIRE_NEXTRIGHT;
    if (b.my < a.my) return WIRE_NEXTUP;
    return WIRE_NEXTDOWN;
  }

  function pathAnimFromDir(bPrev, bNext, isLast) {
    const v = bPrev | bNext;
    if (!isLast) {
      switch (v) {
        case WIRE_PREVUP | WIRE_NEXTDOWN:
        case WIRE_PREVDOWN | WIRE_NEXTUP: return 0;
        case WIRE_PREVLEFT | WIRE_NEXTRIGHT:
        case WIRE_NEXTLEFT | WIRE_PREVRIGHT: return 1;
        case WIRE_PREVLEFT | WIRE_NEXTUP:
        case WIRE_PREVUP | WIRE_NEXTLEFT: return 3;
        case WIRE_PREVLEFT | WIRE_NEXTDOWN:
        case WIRE_PREVDOWN | WIRE_NEXTLEFT: return 5;
        case WIRE_PREVRIGHT | WIRE_NEXTUP:
        case WIRE_PREVUP | WIRE_NEXTRIGHT: return 2;
        case WIRE_PREVRIGHT | WIRE_NEXTDOWN:
        case WIRE_PREVDOWN | WIRE_NEXTRIGHT: return 4;
        default: return 0;
      }
    }
    switch (v) {
      case WIRE_PREVUP | WIRE_NEXTDOWN: return 7;
      case WIRE_PREVDOWN | WIRE_NEXTUP: return 6;
      case WIRE_PREVLEFT | WIRE_NEXTRIGHT: return 8;
      case WIRE_NEXTLEFT | WIRE_PREVRIGHT: return 9;
      case WIRE_PREVLEFT | WIRE_NEXTUP: return 14;
      case WIRE_PREVUP | WIRE_NEXTLEFT: return 15;
      case WIRE_PREVLEFT | WIRE_NEXTDOWN: return 12;
      case WIRE_PREVDOWN | WIRE_NEXTLEFT: return 13;
      case WIRE_PREVRIGHT | WIRE_NEXTUP: return 17;
      case WIRE_PREVUP | WIRE_NEXTRIGHT: return 16;
      case WIRE_PREVRIGHT | WIRE_NEXTDOWN: return 11;
      case WIRE_PREVDOWN | WIRE_NEXTRIGHT: return 10;
      default: return 0;
    }
  }

  // Anim set 0-17 maps to sprite sheet frame (from Java AddWireAnimations)
  const WIRE_ANIM_TO_FRAME = [3, 4, 0, 2, 1, 5, 6, 13, 7, 14, 11, 17, 15, 10, 16, 9, 12, 8];

  function clearPath() {
    path = [];
    pathAnim = [];
  }
  function traceRoute(fromMx, fromMy, toMx, toMy) {
    path = [];
    pathAnim = [];
    if (fromMx < 0 || fromMy < 0 || !matrix[index(fromMx, fromMy)]) return false;
    const p = findPath(fromMx, fromMy, toMx, toMy);
    if (!p || p.length < 2) return false;
    path = p;
    const n = p.length;
    for (let i = 1; i < n - 1; i++) {
      const bPrev = dirPrev(p[i - 1], p[i]);
      const bNext = dirNext(p[i], p[i + 1]);
      pathAnim[i] = pathAnimFromDir(bPrev, bNext, i === n - 2);
    }
    return true;
  }
  function getPath() {
    return path;
  }
  function getPathAnim() {
    return pathAnim;
  }
  function getWireFrame(segmentIndex) {
    const anim = pathAnim[segmentIndex];
    return (anim >= 0 && anim < WIRE_ANIM_TO_FRAME.length) ? WIRE_ANIM_TO_FRAME[anim] : 0;
  }

  function addToRemoveList(piece) {
    if (!piece || removeList.includes(piece)) return;
    removeList.push(piece);
  }

  function deleteRecursive(mx, my) {
    const idx = index(mx, my);
    const p = matrix[idx];
    if (!p || p.visited) return;
    p.visited = true;
    if (mx + 1 < HELLO_MAT_X && matrix[index(mx + 1, my)] && matrix[index(mx + 1, my)].id === p.id)
      deleteRecursive(mx + 1, my);
    if (mx - 1 >= 0 && matrix[index(mx - 1, my)] && matrix[index(mx - 1, my)].id === p.id)
      deleteRecursive(mx - 1, my);
    if (my + 1 < HELLO_MAT_Y && matrix[index(mx, my + 1)] && matrix[index(mx, my + 1)].id === p.id)
      deleteRecursive(mx, my + 1);
    if (my - 1 >= 0 && matrix[index(mx, my - 1)] && matrix[index(mx, my - 1)].id === p.id)
      deleteRecursive(mx, my - 1);
    addToRemoveList(p);
  }

  /** New row at TOP: shift content down, fill row 0 (phones "from above"). Returns true if no room (game over). */
  function createLineFromTop(getRandomPiece) {
    let added = false;
    for (let col = 0; col < HELLO_MAT_X; col++) {
      for (let row = HELLO_MAT_Y - 1; row > 0; row--) {
        const idx = index(col, row);
        const above = index(col, row - 1);
        if (!matrix[idx] && matrix[above]) {
          matrix[idx] = matrix[above];
          matrix[above] = null;
          if (matrix[idx]) {
            matrix[idx].my = row;
            matrix[idx].index = idx;
          }
        }
      }
      if (!matrix[index(col, 0)]) {
        const p = getRandomPiece();
        if (!p) return true;
        p.mx = col;
        p.my = 0;
        p.index = index(col, 0);
        matrix[index(col, 0)] = p;
        added = true;
      }
    }
    return !added;
  }

  /** New row at BOTTOM: shift content up (row i <- row i+1), fill row 9 (phones "from below"). Returns true if no room (game over). */
  function createLineFromBottom(getRandomPiece) {
    let added = false;
    for (let col = 0; col < HELLO_MAT_X; col++) {
      for (let row = 0; row < HELLO_MAT_Y - 1; row++) {
        const idx = index(col, row);
        const fromIdx = index(col, row + 1);
        matrix[idx] = matrix[fromIdx];
        matrix[fromIdx] = null;
        if (matrix[idx]) {
          matrix[idx].my = row;
          matrix[idx].index = idx;
        }
      }
      if (!matrix[index(col, HELLO_MAT_Y - 1)]) {
        const p = getRandomPiece();
        if (!p) return true;
        p.mx = col;
        p.my = HELLO_MAT_Y - 1;
        p.index = index(col, HELLO_MAT_Y - 1);
        matrix[index(col, HELLO_MAT_Y - 1)] = p;
        added = true;
      }
    }
    return !added;
  }

  /** New column on LEFT: shift each row right (col 1←0, 2←1, …), fill col 0. Returns true if no room (game over). */
  function createColumnFromLeft(getRandomPiece) {
    let added = false;
    for (let row = 0; row < HELLO_MAT_Y; row++) {
      for (let col = HELLO_MAT_X - 1; col > 0; col--) {
        const idx = index(col, row);
        const fromIdx = index(col - 1, row);
        if (!matrix[idx] && matrix[fromIdx]) {
          matrix[idx] = matrix[fromIdx];
          matrix[fromIdx] = null;
          if (matrix[idx]) {
            matrix[idx].mx = col;
            matrix[idx].index = idx;
          }
        }
      }
      if (!matrix[index(0, row)]) {
        const p = getRandomPiece();
        if (!p) return true;
        p.mx = 0;
        p.my = row;
        p.index = index(0, row);
        matrix[index(0, row)] = p;
        added = true;
      }
    }
    return !added;
  }

  /** New column on RIGHT: shift each row left (col 0←1, 1←2, …), fill col 8. Returns true if no room (game over). */
  function createColumnFromRight(getRandomPiece) {
    let added = false;
    for (let row = 0; row < HELLO_MAT_Y; row++) {
      for (let col = 0; col < HELLO_MAT_X - 1; col++) {
        const idx = index(col, row);
        const fromIdx = index(col + 1, row);
        if (!matrix[idx] && matrix[fromIdx]) {
          matrix[idx] = matrix[fromIdx];
          matrix[fromIdx] = null;
          if (matrix[idx]) {
            matrix[idx].mx = col;
            matrix[idx].index = idx;
          }
        }
      }
      if (!matrix[index(HELLO_MAT_X - 1, row)]) {
        const p = getRandomPiece();
        if (!p) return true;
        p.mx = HELLO_MAT_X - 1;
        p.my = row;
        p.index = index(HELLO_MAT_X - 1, row);
        matrix[index(HELLO_MAT_X - 1, row)] = p;
        added = true;
      }
    }
    return !added;
  }

  /** Kept for compatibility; same as createLineFromTop. */
  function createLine(getRandomPiece) {
    return createLineFromTop(getRandomPiece);
  }

  function updateRemoveList(dt, onRemove) {
    const FADE_FPS = 30;
    const FADE_FRAMES = 10;

    removeTimer += dt * 1000;
    while (removeTimer >= 80 && removeList.length > 0) {
      removeTimer -= 80;
      const p = removeList.shift();
      if (matrix[p.index] === p) {
        p.effect = 'fade';
        p.fadeFrame = 0;
      }
    }

    for (let i = 0; i < HELLO_MAT_SIZE; i++) {
      const p = matrix[i];
      if (!p || p.effect !== 'fade') continue;
      p.fadeFrame += dt * FADE_FPS;
      if (p.fadeFrame >= FADE_FRAMES) {
        matrix[i] = null;
        onRemove && onRemove(p);
      }
    }
  }

  function countPieces() {
    let n = 0;
    for (let i = 0; i < HELLO_MAT_SIZE; i++) if (matrix[i]) n++;
    return n;
  }

  /** One insert from current side (0=Left, 1=Top, 2=Right, 3=Bottom). Returns true if no room (game over). */
  function insertPhones(getRandomPiece, side) {
    switch (side) {
      case 0: return createColumnFromLeft(getRandomPiece);
      case 1: return createLineFromTop(getRandomPiece);
      case 2: return createColumnFromRight(getRandomPiece);
      case 3: return createLineFromBottom(getRandomPiece);
      default: return createLineFromTop(getRandomPiece);
    }
  }

  return {
    matrix,
    toScreen,
    fromScreen,
    clearVisited,
    deleteRecursive,
    canSelect,
    getNearestConnectionPos,
    findPath,
    clearPath,
    traceRoute,
    getPath,
    getPathAnim,
    getWireFrame,
    createLine,
    createLineFromTop,
    createLineFromBottom,
    createColumnFromLeft,
    createColumnFromRight,
    insertPhones,
    updateRemoveList,
    countPieces,
    get matrixRef() { return matrix; },
  };
}

function createHelloGame(canvas, ui) {
  const ctx = canvas.getContext('2d');
  const width = HELLO.CANVAS_WIDTH;
  const height = HELLO.CANVAS_HEIGHT;

  let state = 'main'; // main, pregame, game, levelup, gameover, congrats
  let currentLevel = 0;
  let points = 0;
  let phonesCollected = 0;
  let phonesToCollect = HELLO.PHONES_TO_COLLECT;
  let newLineTimer = 0;
  let newLineLimit = HELLO.NEW_LINE_MS;
  let insertSide = 0; // 0=Left, 1=Top, 2=Right, 3=Bottom (matches Java bySide after increment)
  let freePieces = [];
  let board;
  let lastState = '';
  let assets = null;
  let currPhone = null;   // { mx, my, idx } when a phone is selected for connection
  let connectPos = null;  // { mx, my } nearest selectable to mouse (path end)
  let routeOK = false;    // true when path exists from currPhone to connectPos
  let lastMouse = null;   // { mx, my } last mouse cell for path updates

  const PHONE_IMGS = ['spr_phone1.gif', 'spr_phone2.gif', 'spr_phone3.gif', 'spr_phone4.gif'];

  function rand() {
    return Math.floor(Math.random() * 0x7fffffff);
  }

  function getLevelConfig() {
    return HELLO.LEVELS[Math.min(currentLevel, HELLO.LEVELS.length - 1)] || HELLO.LEVELS[HELLO.LEVELS.length - 1];
  }

  function createPhonePiece(id) {
    return { id: (id || (rand() % 4)) + 1, mx: 0, my: 0, index: 0, visited: false, effect: 'none', fadeFrame: 0 };
  }

  function getRandomPiece() {
    const id = rand() % 4;
    let p = freePieces.find(px => px.id === id + 1);
    if (p) {
      freePieces = freePieces.filter(px => px !== p);
      p.visited = false;
      p.mx = p.my = 0;
      p.index = 0;
      p.effect = 'none';
      p.fadeFrame = 0;
      return p;
    }
    return createPhonePiece(id);
  }

  function initLevel() {
    const cfg = getLevelConfig();
    phonesToCollect = cfg.phonesToCollect;
    newLineLimit = cfg.newLineMs;
    newLineTimer = newLineLimit;
    phonesCollected = 0;
    state = 'pregame';
  }

  /** Initial board fill: 4 inserts in order Top, Right, Bottom, Left (matches Java InitGameMode). */
  function runInitialInserts() {
    for (let k = 0; k < 4; k++) {
      insertSide = (insertSide + 1) % 4;
      const gameOver = board.insertPhones(getRandomPiece, insertSide);
      if (gameOver) {
        state = 'gameover';
        return;
      }
      AudioManager.helloNewPhones();
    }
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

  function getScorePanelLayout() {
    var pad = HELLO.PANEL_BOX_PAD != null ? HELLO.PANEL_BOX_PAD : 10;
    var lineH = HELLO.PANEL_LINE_HEIGHT;
    var boxW = HELLO.PANEL_BOX_W != null ? HELLO.PANEL_BOX_W : 112;
    var panelLeft = HELLO.RIGHT_PANEL_LEFT != null ? HELLO.RIGHT_PANEL_LEFT : 296;
    var panelRight = HELLO.RIGHT_PANEL_RIGHT != null ? HELLO.RIGHT_PANEL_RIGHT : HELLO.CANVAS_WIDTH;
    var panelCenterX = (panelLeft + panelRight) / 2;
    var boxX = Math.round(panelCenterX - boxW / 2);
    var boxY = HELLO.PANEL_BOX_Y;
    var boxH = lineH * 3 + pad * 2;
    var labelX = boxX + pad;
    var valueColW = HELLO.PANEL_VALUE_COLUMN_WIDTH != null ? HELLO.PANEL_VALUE_COLUMN_WIDTH : 50;
    var valuePadR = HELLO.PANEL_VALUE_PAD_RIGHT != null ? HELLO.PANEL_VALUE_PAD_RIGHT : pad;
    var valueX = boxX + boxW - valuePadR - valueColW;
    var textY = boxY + pad;
    return { labelX: labelX, valueX: valueX, textY: textY, boxX: boxX, boxY: boxY, boxW: boxW, boxH: boxH, pad: pad, lineH: lineH };
  }
  function drawScorePanelBox() {
    if (state !== 'game' && state !== 'pregame') return;
    var L = getScorePanelLayout();
    ctx.fillStyle = 'rgba(30,45,80,0.92)';
    ctx.fillRect(L.boxX, L.boxY, L.boxW, L.boxH);
    ctx.strokeStyle = 'rgba(255,255,255,0.45)';
    ctx.lineWidth = 1;
    ctx.strokeRect(L.boxX, L.boxY, L.boxW, L.boxH);
  }
  function drawScorePanelText() {
    if (state !== 'game' && state !== 'pregame') return;
    var L = getScorePanelLayout();
    var labelX = L.labelX, valueX = L.valueX, y = L.textY;
    ctx.font = 'bold 13px sans-serif';
    ctx.textBaseline = 'top';
    ctx.textAlign = 'left';
    ctx.fillStyle = 'rgba(0,0,0,0.6)';
    ctx.fillText('Level', labelX + 1, y + 1);
    ctx.fillText('Score', labelX + 1, y + L.lineH + 1);
    ctx.fillText('Phones', labelX + 1, y + L.lineH * 2 + 1);
    ctx.fillStyle = '#fff';
    ctx.fillText('Level', labelX, y);
    ctx.fillText('Score', labelX, y + L.lineH);
    ctx.fillText('Phones', labelX, y + L.lineH * 2);
    ctx.font = 'bold 14px sans-serif';
    ctx.fillStyle = 'rgba(0,0,0,0.5)';
    ctx.fillText('' + (currentLevel + 1), valueX + 1, y + 1);
    ctx.fillText('' + points, valueX + 1, y + L.lineH + 1);
    ctx.fillText(phonesCollected + ' / ' + phonesToCollect, valueX + 1, y + L.lineH * 2 + 1);
    ctx.fillStyle = '#ffc107';
    ctx.fillText('' + (currentLevel + 1), valueX, y);
    ctx.fillText('' + points, valueX, y + L.lineH);
    ctx.fillText(phonesCollected + ' / ' + phonesToCollect, valueX, y + L.lineH * 2);
  }

  function draw() {
    const bkg = assets && getImg('bkg_Interface.gif');
    if (bkg) {
      ctx.drawImage(bkg, 0, 0, width, height);
    } else {
      ctx.fillStyle = '#1e3a5f';
      ctx.fillRect(0, 0, width, height);
      ctx.fillStyle = '#2d4a6f';
      ctx.fillRect(HELLO.UP_LEFT_X - 4, HELLO.UP_LEFT_Y - 4, HELLO_MAT_X * HELLO_PIECE + 8, HELLO_MAT_Y * HELLO_PIECE + 8);
      ctx.strokeStyle = '#4a6fa0';
      ctx.lineWidth = 2;
      ctx.strokeRect(HELLO.UP_LEFT_X - 4, HELLO.UP_LEFT_Y - 4, HELLO_MAT_X * HELLO_PIECE + 8, HELLO_MAT_Y * HELLO_PIECE + 8);
    }
    drawScorePanelBox();

    // 1) Phones first (below UI); fading pieces use phone_end sprite animation
    const PHONE_END_SIZE = 49;
    const PHONE_END_OFFSET = (HELLO_PIECE - PHONE_END_SIZE) / 2;
    for (let i = 0; i < HELLO_MAT_SIZE; i++) {
      const p = board.matrixRef[i];
      if (!p) continue;
      const pos = board.toScreen(p.mx, p.my);

      if (p.effect === 'fade') {
        const endName = 'spr_phone' + ((p.id - 1) % 4 + 1) + '_end.gif';
        const endImg = assets && getImg(endName);
        const frame = Math.min(9, Math.floor(p.fadeFrame));
        if (endImg) {
          const cols = Math.max(1, Math.floor(endImg.width / PHONE_END_SIZE));
          const sx = (frame % cols) * PHONE_END_SIZE;
          const sy = Math.floor(frame / cols) * PHONE_END_SIZE;
          ctx.drawImage(endImg, sx, sy, PHONE_END_SIZE, PHONE_END_SIZE,
            pos.x + PHONE_END_OFFSET, pos.y + PHONE_END_OFFSET, PHONE_END_SIZE, PHONE_END_SIZE);
        } else {
          ctx.globalAlpha = 1 - p.fadeFrame / 10;
          ctx.fillStyle = HELLO.COLORS[(p.id - 1) % 4];
          ctx.fillRect(pos.x + 2, pos.y + 2, HELLO_PIECE - 4, HELLO_PIECE - 4);
          ctx.globalAlpha = 1;
        }
        continue;
      }

      const imgName = PHONE_IMGS[(p.id - 1) % 4];
      const img = assets && getImg(imgName);
      if (img) {
        ctx.drawImage(img, 0, 0, HELLO_PIECE, HELLO_PIECE, pos.x, pos.y, HELLO_PIECE, HELLO_PIECE);
      } else {
        ctx.fillStyle = HELLO.COLORS[(p.id - 1) % 4];
        const r = 6, x = pos.x + 2, y = pos.y + 2, w = HELLO_PIECE - 4, h = HELLO_PIECE - 4;
        ctx.beginPath();
        ctx.moveTo(x + r, y);
        ctx.lineTo(x + w - r, y);
        ctx.quadraticCurveTo(x + w, y, x + w, y + r);
        ctx.lineTo(x + w, y + h - r);
        ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
        ctx.lineTo(x + r, y + h);
        ctx.quadraticCurveTo(x, y + h, x, y + h - r);
        ctx.lineTo(x, y + r);
        ctx.quadraticCurveTo(x, y, x + r, y);
        ctx.fill();
        ctx.strokeStyle = 'rgba(255,255,255,0.5)';
        ctx.lineWidth = 1;
        ctx.stroke();
      }
    }

    // 2) Selection ring on selected phone
    if (currPhone && board.matrixRef[currPhone.idx]) {
      const pos = board.toScreen(currPhone.mx, currPhone.my);
      const selImg = assets && getImg('spr_Selection.gif');
      if (selImg) {
        ctx.drawImage(selImg, 0, 0, HELLO_PIECE, HELLO_PIECE, pos.x, pos.y, HELLO_PIECE, HELLO_PIECE);
      } else {
        ctx.strokeStyle = '#fff';
        ctx.lineWidth = 3;
        ctx.strokeRect(pos.x + 1, pos.y + 1, HELLO_PIECE - 2, HELLO_PIECE - 2);
      }
    }

    // 3) Phone line (wire sprites) from selected phone to connection point
    const path = board.getPath();
    if (path && path.length >= 2) {
      const pathId = currPhone && board.matrixRef[currPhone.idx]
        ? (board.matrixRef[currPhone.idx].id - 1) % 4
        : 0;
      const wireImg = assets && getImg('spr_wire' + (pathId + 1) + '.gif');
      const fw = HELLO_PIECE;
      const fh = HELLO_PIECE;
      for (let i = 1; i < path.length - 1; i++) {
        const pos = board.toScreen(path[i].mx, path[i].my);
        const frame = board.getWireFrame(i);
        if (wireImg) {
          const cols = Math.max(1, Math.floor(wireImg.width / fw));
          const sx = (frame % cols) * fw;
          const sy = Math.floor(frame / cols) * fh;
          ctx.drawImage(wireImg, sx, sy, fw, fh, pos.x, pos.y, fw, fh);
        } else {
          ctx.fillStyle = HELLO.COLORS[pathId] || '#fff';
          ctx.fillRect(pos.x, pos.y, fw, fh);
        }
      }
    }

    // 4) Next line bar and level bar ON TOP (sprite clipping like original)
    const nx = HELLO.NEXT_LINE_X, ny = HELLO.NEXT_LINE_Y, nw = HELLO.NEXT_LINE_W, nh = HELLO.NEXT_LINE_H;
    const nextLineImg = assets && getImg('spr_NextLine.gif');
    if (nextLineImg && state === 'game') {
      const sw = Math.min(nextLineImg.width, nw);
      const sh = Math.min(nextLineImg.height, nh);
      ctx.drawImage(nextLineImg, 0, 0, sw, sh, nx, ny, nw, nh);
      const fillW = (newLineLimit > 0) ? (nw * (newLineTimer / newLineLimit)) : 0;
      if (fillW > 0) {
        const srcFillW = (sw * fillW) / nw;
        ctx.drawImage(nextLineImg, 0, 0, srcFillW, sh, nx, ny, fillW, nh);
      }
    } else if (state === 'game') {
      ctx.fillStyle = '#3a4a5a';
      ctx.fillRect(nx, ny, nw, nh);
      ctx.fillStyle = 'rgba(255, 220, 100, 0.6)';
      ctx.fillRect(nx, ny, (newLineLimit > 0 ? nw * (newLineTimer / newLineLimit) : 0), nh);
    }

    // 5) Level completion bar (spr_LevelBar: left 14px = track, right 14px = gradient fill)
    const lx = HELLO.LEVEL_BAR_X, ly = HELLO.LEVEL_BAR_Y, lw = HELLO.LEVEL_BAR_W, lh = HELLO.LEVEL_BAR_H;
    const levelBarImg = assets && getImg('spr_LevelBar.gif');
    if (levelBarImg && (state === 'game' || state === 'pregame')) {
      var barH = Math.min(levelBarImg.height, lh);
      var trackW = Math.min(14, Math.floor(levelBarImg.width / 2));
      var fillW = levelBarImg.width > 14 ? Math.min(14, levelBarImg.width - trackW) : trackW;
      ctx.drawImage(levelBarImg, 0, 0, trackW, barH, lx, ly, lw, lh);
      var progress = phonesToCollect > 0 ? phonesCollected / phonesToCollect : 0;
      var fillH = barH * Math.min(1, progress);
      if (fillH > 0) {
        var srcFillH = (levelBarImg.height * fillH) / barH;
        ctx.drawImage(levelBarImg, trackW, levelBarImg.height - srcFillH, fillW, srcFillH, lx, ly + lh - fillH, lw, fillH);
      }
    }

    // 6) Ringo character – right side, near bottom
    if (state === 'game' || state === 'pregame') {
      var ringoImg = getImg('RingoStopped.gif');
      if (ringoImg) {
        ctx.drawImage(ringoImg, 0, 0, ringoImg.width, ringoImg.height,
          HELLO.RINGO_X, HELLO.RINGO_Y, HELLO.RINGO_W, HELLO.RINGO_H);
      }
    }

    // 7) Score panel text on top (box already drawn behind after background)
    drawScorePanelText();
  }

  function update(dt) {
    if (state !== 'pregame' && state !== 'game') return;

    if (state === 'game' && currPhone && board.matrixRef[currPhone.idx]) {
      if (lastMouse) {
        connectPos = board.getNearestConnectionPos(lastMouse.mx, lastMouse.my);
        if (connectPos) {
          routeOK = board.traceRoute(currPhone.mx, currPhone.my, connectPos.mx, connectPos.my);
        } else {
          board.clearPath();
          routeOK = false;
        }
      }
    } else {
      routeOK = false;
    }

    board.updateRemoveList(dt, (p) => {
      if (currPhone && (p.mx === currPhone.mx && p.my === currPhone.my)) {
        currPhone = null;
        connectPos = null;
        board.clearPath();
      }
      freePieces.push(p);
      points += (HELLO.POINTS_PER_PHONE || 321);
      phonesCollected++;
      AudioManager.helloPhoneRemoved();
    });

    if (state === 'game') {
      newLineTimer += dt * 1000;
      if (newLineTimer >= newLineLimit) {
        newLineTimer = 0;
        insertSide = (insertSide + 1) % 4; // Left(0) -> Top(1) -> Right(2) -> Bottom(3)
        const gameOver = board.insertPhones(getRandomPiece, insertSide);
        if (gameOver) {
          state = 'gameover';
          return;
        }
        AudioManager.helloNewPhones();
      }
    } else if (state === 'pregame') {
      state = 'game';
      currPhone = null;
      connectPos = null;
      lastMouse = null;
      board.clearPath();
      runInitialInserts();
    }

    if (phonesCollected >= phonesToCollect) {
      state = currentLevel >= HELLO.LEVELS.length - 1 ? 'congrats' : 'levelup';
    }
  }

  function onMouseMove(mx, my) {
    if (state !== 'game') return;
    const cell = board.fromScreen(mx, my);
    lastMouse = cell ? { mx: cell.mx, my: cell.my } : null;
  }

  function onClick(mx, my) {
    if (state === 'main') {
      initLevel();
      ui.hideMenu();
      ui.showHud();
      ui.updateHud(1, phonesCollected, phonesToCollect, points);
      return;
    }
    if (state === 'game') {
      const cell = board.fromScreen(mx, my);
      if (!cell) return;

      if (!currPhone) {
        if (board.matrixRef[cell.idx] && board.canSelect(cell.mx, cell.my)) {
          currPhone = { mx: cell.mx, my: cell.my, idx: cell.idx };
          AudioManager.helloClickPhone();
        }
        board.clearPath();
        return;
      }

      if (cell.mx === currPhone.mx && cell.my === currPhone.my) {
        currPhone = null;
        connectPos = null;
        board.clearPath();
        return;
      }

      const targetPiece = board.matrixRef[cell.idx];
      const currPiece = board.matrixRef[currPhone.idx];
      const sameColor = targetPiece && currPiece && targetPiece.id === currPiece.id;

      if (sameColor && board.traceRoute(currPhone.mx, currPhone.my, cell.mx, cell.my)) {
        AudioManager.helloClickPhone();
        board.clearVisited();
        board.deleteRecursive(currPhone.mx, currPhone.my);
        board.deleteRecursive(cell.mx, cell.my);
        currPhone = null;
        connectPos = null;
        board.clearPath();
        return;
      }

      if (board.matrixRef[cell.idx] && board.canSelect(cell.mx, cell.my)) {
        currPhone = { mx: cell.mx, my: cell.my, idx: cell.idx };
        AudioManager.helloClickPhone();
      }
      board.clearPath();
    }
  }

  function init(loadedAssets) {
    assets = loadedAssets || null;
    board = createHelloBoard();
    state = 'main';
    currentLevel = 0;
    points = 0;
    phonesCollected = 0;
    currPhone = null;
    connectPos = null;
    lastMouse = null;
    routeOK = false;
  }

  return {
    init,
    update,
    draw,
    onClick,
    onMouseMove,
    getState: () => state,
    getPoints: () => points,
    getPhonesCollected: () => phonesCollected,
    getPhonesToCollect: () => phonesToCollect,
    getCurrentLevel: () => currentLevel,
    showLevelUp() {
      currentLevel++;
      initLevel();
      state = 'pregame';
      ui.hidePopup();
      ui.updateHud(currentLevel + 1, phonesCollected, phonesToCollect, points);
    },
    showGameOver() {
      currentLevel = 0;
      initLevel();
      state = 'pregame';
      ui.hidePopup();
      ui.updateHud(1, phonesCollected, phonesToCollect, points);
    },
    showCongrats() {
      currentLevel = 0;
      initLevel();
      state = 'pregame';
      ui.hidePopup();
      ui.updateHud(1, phonesCollected, phonesToCollect, points);
    },
    setLevelComplete() {
      state = currentLevel >= HELLO.LEVELS.length - 1 ? 'congrats' : 'initlevel';
    },
    setPaused: () => {},
    isPaused: () => false,
  };
}
