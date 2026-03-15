function JGDLMain(canvas, options) {
  options = options || {};
  this.canvas = canvas;
  this.VideoManager = new JGDLVideoManager(canvas);
  this.InputManager = new JGDLInputManager(canvas);
  this.TimeHandler = new JGDLTimeHandler();
  this.SoundManager = new JGDLSoundManager();
  this.VideoManager.pr_Main = this;
  this.InputManager.pr_Main = this;
  this.SoundManager.pr_Main = this;

  this.ImagesDir = options.imagesDir || 'Surfaces/';
  this.SoundsDir = options.soundsDir || 'Sounds/';
  this.GameName = options.gameName || 'Game';
  this.VideoManager.VideoSize.fx = canvas.width;
  this.VideoManager.VideoSize.fy = canvas.height;

  this._imageList = [];
  this._soundList = [];
  this._imageCache = {};
  this.pr_CurScene = null;
  this.bPaused = false;
  this.bRunning = false;
  this.Randomizer = { nextInt: function () { return Math.floor(Math.random() * 0x7fffffff); }, nextDouble: function () { return Math.random(); } };

  this.AddImage = function (filename) {
    this._imageList.push(this.ImagesDir + filename);
  };
  this.AddSound = function (filename) {
    this._soundList.push(this.SoundsDir + filename);
  };

  this.getParameter = function (name) {
    return options.params && options.params[name] != null ? options.params[name] : '';
  };

  var self = this;

  this.LoadResources = function (baseUrl) {
    baseUrl = baseUrl || '';
    return new Promise(function (resolve, reject) {
      var total = self._imageList.length + self._soundList.length;
      var done = 0;
      var timeout = setTimeout(function () { doResolve(); }, 15000);
      function doResolve() {
        clearTimeout(timeout);
        resolve();
      }
      function next() {
        done++;
        var pct = total ? (done / total) * 100 : 100;
        self.VideoManager.DrawLoading(pct, self.GameName);
        if (done >= total) doResolve();
      }

      self.SoundManager.Initialize();
      self._imageCache = {};

      function loadImages() {
        if (!self._imageList.length) { loadSounds(); return; }
        var idx = 0;
        function doOne() {
          if (idx >= self._imageList.length) { loadSounds(); return; }
          var path = self._imageList[idx];
          var url = baseUrl ? (baseUrl + path) : path;
          var img = new Image();
          img.onload = function () {
            self._imageCache[path] = img;
            var fs = null;
            if (path.indexOf('bkg_') === path.lastIndexOf('/') + 1 || path.indexOf('spr_Main') >= 0)
              fs = { fx: self.VideoManager.VideoSize.fx, fy: self.VideoManager.VideoSize.fy };
            self.VideoManager.RegisterImage(path, img, fs || { fx: img.width, fy: img.height });
            next();
            idx++;
            doOne();
          };
          img.onerror = function () {
            idx++;
            next();
            doOne();
          };
          img.src = url;
        }
        doOne();
      }

      function loadSounds() {
        if (!self._soundList.length) { if (done >= total) doResolve(); return; }
        var idx = 0;
        var ctx = self.SoundManager.audioCtx;
        function doOne() {
          if (idx >= self._soundList.length) { if (done >= total) doResolve(); return; }
          var path = self._soundList[idx];
          var url = baseUrl ? (baseUrl + path) : path;
          if (!ctx) {
            idx++;
            next();
            doOne();
            return;
          }
          fetch(url).then(function (r) { return r.arrayBuffer(); }).then(function (buf) {
            if (path.toLowerCase().endsWith('.au')) {
              var decoded = AssetLoader.decodeAu(buf, ctx);
              if (decoded) self.SoundManager.RegisterSound(path, decoded);
            }
            next();
            idx++;
            doOne();
          }).catch(function () {
            idx++;
            next();
            doOne();
          });
        }
        doOne();
      }

      if (!total) doResolve();
      else loadImages();
    });
  };

  this.SetCurrentScene = function (scene) {
    if (this.pr_CurScene) {
      this.pr_CurScene.Release();
      this.pr_CurScene = null;
    }
    this.pr_CurScene = scene;
    if (scene) {
      scene.pr_Main = this;
      scene.Initialize();
      this.TimeHandler.Reset();
    }
  };

  this.Pause = function () { this.bPaused = true; };
  this.Resume = function () { this.bPaused = false; this.TimeHandler.Reset(); };
  this.IsPaused = function () { return this.bPaused; };

  this.Loop = function () {
    if (!this.bRunning) return;
    this.InputManager.Read();
    this.TimeHandler.Update();
    this.VideoManager.Clear('#000');
    if (this.pr_CurScene) {
      this.pr_CurScene.Draw();
      if (!this.bPaused) {
        this.pr_CurScene.Update();
        this.pr_CurScene.Execute();
      }
      if (options.afterExecute) options.afterExecute(this);
    }
    if (options.drawPause && this.bPaused) options.drawPause(this.VideoManager.ctx);
    requestAnimationFrame(function () { self.Loop(); });
  };

  this.Start = function (baseUrl) {
    var self = this;
    if (options.addResources) options.addResources.call(this);
    this.VideoManager.DrawLoading(0, this.GameName);
    this.LoadResources(baseUrl).then(function () {
      if (options.initGame) options.initGame(self);
      self.bRunning = true;
      self.Loop();
    }).catch(function (e) {
      console.warn('Load failed', e);
      if (options.initGame) options.initGame(self);
      self.bRunning = true;
      self.Loop();
    });
  };
}
