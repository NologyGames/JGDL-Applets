(function () {
  if (typeof window.JGDL !== 'undefined') return;
  var base = (function () {
    var s = document.currentScript.src;
    return s ? s.replace(/\/[^/]+$/, '/') : '';
  })();
  var scripts = [
    'jgdl-vector.js',
    'jgdl-time.js',
    'jgdl-input.js',
    'jgdl-video.js',
    'jgdl-sound.js',
    'jgdl-animation.js',
    'jgdl-sprite.js',
    'jgdl-layer.js',
    'jgdl-scene.js',
    'jgdl-main.js'
  ];
  var i = 0;
  function loadNext() {
    if (i >= scripts.length) {
      window.JGDL = {
        JGDLVector: JGDLVector,
        JGDLTimeHandler: JGDLTimeHandler,
        JGDLTimeAccumulator: JGDLTimeAccumulator,
        JGDLInputManager: JGDLInputManager,
        JGDLVideoManager: JGDLVideoManager,
        JGDLSoundManager: JGDLSoundManager,
        JGDLAnimation: JGDLAnimation,
        JGDLSprite: JGDLSprite,
        JGDLLayer: JGDLLayer,
        JGDLScene: JGDLScene,
        JGDLMain: JGDLMain
      };
      if (window.onJGDLReady) window.onJGDLReady();
      return;
    }
    var s = document.createElement('script');
    s.src = base + scripts[i];
    s.onload = function () { i++; loadNext(); };
    s.onerror = function () { i++; loadNext(); };
    document.head.appendChild(s);
  }
  loadNext();
})();
