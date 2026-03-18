(function () {
  const canvas = document.getElementById('game-canvas');
  if (!canvas) return;

  const loadingEl = document.getElementById('loading');
  const menuEl = document.getElementById('main-menu');

  const overlayEl = document.getElementById('ui-overlay');
  const ui = {
    menu: document.getElementById('main-menu'),
    hud: document.getElementById('hud'),
    popup: document.getElementById('popup'),
    popupTitle: document.getElementById('popup-title'),
    btnPlayAgain: document.getElementById('btn-play-again'),
    btnNextLevel: document.getElementById('btn-next-level'),
    pauseOverlay: document.getElementById('pause-overlay'),
    hideMenu() {
      if (this.menu) this.menu.classList.add('hidden');
      if (overlayEl) overlayEl.style.pointerEvents = 'none';
    },
    showHud() {
      if (this.hud) this.hud.classList.remove('hidden');
    },
    updateHud(level, found, target, score) {
      const levelNum = document.getElementById('level-num');
      const foundEl = document.getElementById('found');
      const targetEl = document.getElementById('target');
      const scoreEl = document.getElementById('score');
      if (levelNum) levelNum.textContent = level;
      if (foundEl) foundEl.textContent = found;
      if (targetEl) targetEl.textContent = target;
      if (scoreEl) scoreEl.textContent = score;
    },
    showPopup(title, showNextLevel) {
      if (overlayEl) overlayEl.style.pointerEvents = 'auto';
      if (this.popup) this.popup.classList.remove('hidden');
      if (this.popupTitle) this.popupTitle.textContent = title;
      if (this.btnNextLevel) this.btnNextLevel.classList.toggle('hidden', !showNextLevel);
    },
    hidePopup() {
      if (this.popup) this.popup.classList.add('hidden');
      if (overlayEl) overlayEl.style.pointerEvents = 'none';
    },
  };

  const game = createPopcornGame(canvas, ui);
  var lastState = '';

  function attachHandlers() {
    canvas.addEventListener('click', function handleCanvasClick(e) {
      const rect = canvas.getBoundingClientRect();
      const scaleX = canvas.width / rect.width;
      const scaleY = canvas.height / rect.height;
      const mx = (e.clientX - rect.left) * scaleX;
      const my = (e.clientY - rect.top) * scaleY;
      if (game.isPaused()) {
        game.setPaused(false);
        if (ui.pauseOverlay) ui.pauseOverlay.classList.add('hidden');
        return;
      }
      game.onClick(mx, my);
    });
    function startIfMain(e) {
      var state = game.getState();
      if (window._debug) window._debug('overlay click, state=' + state);
      if (state === 'main') {
        e.preventDefault();
        e.stopPropagation();
        if (window._debug) window._debug('starting game');
        game.onClick(canvas.width / 2, canvas.height / 2);
      }
    }
    if (overlayEl) {
      overlayEl.addEventListener('click', startIfMain, true);
      if (window._debug) window._debug('overlay listener attached');
    } else if (window._debug) window._debug('no overlayEl');
    document.addEventListener('keydown', function (e) {
      if (e.key === 'p' || e.key === 'P') {
        if (game.getState() === 'game' || game.getState() === 'pregame') {
          game.setPaused(!game.isPaused());
          if (ui.pauseOverlay) ui.pauseOverlay.classList.toggle('hidden', !game.isPaused());
        }
      }
    });
    if (ui.btnPlayAgain) {
      ui.btnPlayAgain.addEventListener('click', function () {
        game.showGameOver();
        ui.hidePopup();
      });
    }
    if (ui.btnNextLevel) {
      ui.btnNextLevel.addEventListener('click', function () {
        game.showLevelUp();
        ui.hidePopup();
      });
    }
    canvas.focus();
  }

  if (typeof JGDLMain !== 'undefined') {
    if (window._debug) window._debug('using JGDL');
    var main = new JGDLMain(canvas, {
      imagesDir: typeof POPCORN_SURFACES !== 'undefined' ? POPCORN_SURFACES : 'assets/popcorn/surfaces/',
      soundsDir: typeof POPCORN_SOUNDS !== 'undefined' ? POPCORN_SOUNDS : 'assets/popcorn/sounds/',
      gameName: 'Super Popcorn Machine',
      addResources: function () {
        if (typeof POPCORN_IMAGE_NAMES !== 'undefined') {
          for (var i = 0; i < POPCORN_IMAGE_NAMES.length; i++) this.AddImage(POPCORN_IMAGE_NAMES[i]);
        }
        if (typeof POPCORN_SOUND_NAMES !== 'undefined') {
          for (var j = 0; j < POPCORN_SOUND_NAMES.length; j++) this.AddSound(POPCORN_SOUND_NAMES[j]);
        }
      },
      initGame: function (engine) {
        if (window._debug) window._debug('initGame called');
        var scene = {
          pr_Main: engine,
          Initialize: function () {
            game.init(engine);
          },
          Draw: function () {
            game.draw();
          },
          Update: function () {},
          Execute: function () {
            game.update(engine.TimeHandler.fFrameTime);
          },
          Release: function () {},
        };
        engine.SetCurrentScene(scene);
        AudioManager.setPopcornAssets(engine);
        if (loadingEl) loadingEl.classList.add('hidden');
        if (menuEl) menuEl.classList.remove('hidden');
        attachHandlers();
      },
      afterExecute: function (engine) {
        var state = game.getState();
        if (state === 'levelup') game.setLevelComplete();
        if (state !== lastState) {
          if (state === 'initlevel') ui.showPopup('Level Complete!', true);
          else if (state === 'gameover') ui.showPopup('Game Over', false);
          else if (state === 'congrats') ui.showPopup('Congratulations!', false);
          lastState = state;
        }
        if (state === 'initlevel' || state === 'gameover' || state === 'congrats') return;
        ui.updateHud(game.getCurrentLevel() + 1, game.getFoundCorns(), game.getCornsToFind(), game.getPoints());
      },
      drawPause: function (ctx) {
        if (!ctx) return;
        ctx.fillStyle = 'rgba(0,0,0,0.5)';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = '#fff';
        ctx.font = 'italic bold 20px Arial';
        ctx.textAlign = 'center';
        ctx.fillText('CLICK OR PRESS P TO CONTINUE', canvas.width / 2, canvas.height / 2);
      },
    });
    main.Start('');
    return;
  }

  (async function start() {
    if (loadingEl) {
        loadingEl.classList.remove('hidden');
        var textEl = loadingEl.querySelector('.loading-text');
        if (textEl) textEl.textContent = 'Loading...';
      }
    if (menuEl) menuEl.classList.add('hidden');
    var assets = null;
    try {
      assets = await loadPopcornAssets();
      AudioManager.setPopcornAssets(assets);
    } catch (e) {
      console.warn('Assets load failed, using fallbacks', e);
    }
    if (loadingEl) loadingEl.classList.add('hidden');
    if (menuEl) menuEl.classList.remove('hidden');
    game.init(assets);
    runLoop();
    attachHandlers();
  })();

  function runLoop() {
    var lastTime = performance.now();
    function tick() {
      var now = performance.now();
      var dt = Math.min((now - lastTime) / 1000, 0.1);
      lastTime = now;
      var state = game.getState();
      if (state === 'levelup') game.setLevelComplete();
      if (state !== lastState) {
        if (state === 'initlevel') ui.showPopup('Level Complete!', true);
        else if (state === 'gameover') ui.showPopup('Game Over', false);
        else if (state === 'congrats') ui.showPopup('Congratulations!', false);
        lastState = state;
      }
      if (state === 'initlevel' || state === 'gameover' || state === 'congrats') {
        game.update(0);
        game.draw();
        ui.updateHud(game.getCurrentLevel() + 1, game.getFoundCorns(), game.getCornsToFind(), game.getPoints());
        requestAnimationFrame(tick);
        return;
      }
      game.update(dt);
      game.draw();
      ui.updateHud(game.getCurrentLevel() + 1, game.getFoundCorns(), game.getCornsToFind(), game.getPoints());
      requestAnimationFrame(tick);
    }
    requestAnimationFrame(tick);
  }
})();
