function JGDLTimeHandler() {
  this.iFrameTime = 0;
  this.fFrameTime = 0;
  this.iFps = 0;
  this._lastTime = 0;
  this._frameCount = 0;
  this._accum = 0;
}

JGDLTimeHandler.prototype.Update = function () {
  var now = typeof performance !== 'undefined' ? performance.now() : Date.now();
  this.iFrameTime = this._lastTime ? Math.min(100, Math.max(5, now - this._lastTime)) : 16;
  this._lastTime = now;
  this.fFrameTime = this.iFrameTime * 0.001;
  this._accum += this.iFrameTime;
  this._frameCount++;
  if (this._accum >= 1000) {
    this.iFps = this._frameCount;
    this._frameCount = 0;
    this._accum = 0;
  }
};

JGDLTimeHandler.prototype.Reset = function () {
  this._lastTime = typeof performance !== 'undefined' ? performance.now() : Date.now();
};

JGDLTimeHandler.prototype.GetFPS = function () {
  return this.iFps;
};

function JGDLTimeAccumulator() {
  this.pr_Main = null;
  this.iTimeLimit = 0;
  this.iTimeAccum = 0;
}

JGDLTimeAccumulator.prototype.Init = function (main, limitMs) {
  this.pr_Main = main;
  this.iTimeLimit = limitMs;
  this.iTimeAccum = 0;
};

JGDLTimeAccumulator.prototype.Update = function () {
  if (this.pr_Main && this.pr_Main.TimeHandler) {
    this.iTimeAccum += this.pr_Main.TimeHandler.iFrameTime;
  }
};

JGDLTimeAccumulator.prototype.Ended = function () {
  return this.iTimeAccum >= this.iTimeLimit;
};

JGDLTimeAccumulator.prototype.Restart = function () {
  this.iTimeAccum %= this.iTimeLimit;
};
