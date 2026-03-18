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
    hideMenu() {
      if (this.menu) this.menu.classList.add('hidden');
      if (overlayEl) overlayEl.style.pointerEvents = 'none';
    },
    showHud() {
      // Hello draws Level/Score/Phones on canvas; keep HTML HUD hidden
      if (this.hud) this.hud.classList.add('hidden');
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

  const game = createHelloGame(canvas, ui);
  var lastState = '';

  function attachHandlers() {
    function canvasCoords(e) {
      var rect = canvas.getBoundingClientRect();
      var scaleX = canvas.width / rect.width;
      var scaleY = canvas.height / rect.height;
      return {
        mx: (e.clientX - rect.left) * scaleX,
        my: (e.clientY - rect.top) * scaleY,
      };
    }
    canvas.addEventListener('click', function handleCanvasClick(e) {
      var c = canvasCoords(e);
      game.onClick(c.mx, c.my);
    });
    canvas.addEventListener('mousemove', function handleMouseMove(e) {
      var c = canvasCoords(e);
      if (typeof game.onMouseMove === 'function') game.onMouseMove(c.mx, c.my);
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
      imagesDir: typeof HELLO_SURFACES !== 'undefined' ? HELLO_SURFACES : 'assets/hello/surfaces/',
      soundsDir: typeof HELLO_SOUNDS !== 'undefined' ? HELLO_SOUNDS : 'assets/hello/sounds/',
      gameName: 'Hello!',
      addResources: function () {
        if (typeof HELLO_IMAGE_NAMES !== 'undefined') {
          for (var i = 0; i < HELLO_IMAGE_NAMES.length; i++) this.AddImage(HELLO_IMAGE_NAMES[i]);
        }
        if (typeof HELLO_SOUND_NAMES !== 'undefined') {
          for (var j = 0; j < HELLO_SOUND_NAMES.length; j++) this.AddSound(HELLO_SOUND_NAMES[j]);
        }
      },
      initGame: function (engine) {
        if (window._debug) window._debug('initGame called');
        if (typeof AudioManager !== 'undefined') AudioManager.setHelloAssets(engine);
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
        ui.updateHud(game.getCurrentLevel() + 1, game.getPhonesCollected(), game.getPhonesToCollect(), game.getPoints());
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
      assets = await loadHelloAssets();
    } catch (e) {
      console.warn('Hello assets load failed, using fallbacks', e);
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
        ui.updateHud(game.getCurrentLevel() + 1, game.getPhonesCollected(), game.getPhonesToCollect(), game.getPoints());
        requestAnimationFrame(tick);
        return;
      }
      game.update(dt);
      game.draw();
      ui.updateHud(game.getCurrentLevel() + 1, game.getPhonesCollected(), game.getPhonesToCollect(), game.getPoints());
      requestAnimationFrame(tick);
    }
    requestAnimationFrame(tick);
  }
})();
