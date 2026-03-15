function JGDLAnimation() {
  this.pr_Main = null;
  this.bRepeat = false;
  this.Frames = [];
  this.iFramesPerSecond = 1;
  this.uiTimeAccum = 0;
  this._iCurrentFrame = 0;
}

JGDLAnimation.prototype.Update = function () {
  if (!this.pr_Main || !this.Frames.length) return;
  var interval = this.iFramesPerSecond ? 1000 / this.iFramesPerSecond : 1000;
  this.uiTimeAccum += this.pr_Main.TimeHandler.iFrameTime;
  if (this.uiTimeAccum >= interval) {
    var n = Math.floor(this.uiTimeAccum / interval);
    this.uiTimeAccum %= interval;
    if (this.bRepeat) {
      this._iCurrentFrame = (this._iCurrentFrame + n) % this.Frames.length;
    } else {
      this._iCurrentFrame = Math.min(this._iCurrentFrame + n, this.Frames.length);
    }
  }
};

JGDLAnimation.prototype.GetCurrentFrame = function () {
  if (!this.Frames.length) return 0;
  if (this._iCurrentFrame >= this.Frames.length) return this.Frames[this.Frames.length - 1];
  var f = this.Frames[this._iCurrentFrame];
  return typeof f === 'number' ? f : (f == null || f < 0 ? 0 : f);
};

JGDLAnimation.prototype.Ended = function () {
  if (this.bRepeat) return false;
  return this._iCurrentFrame >= this.Frames.length;
};

JGDLAnimation.prototype.Reset = function () {
  this.uiTimeAccum = 0;
  this._iCurrentFrame = 0;
};
