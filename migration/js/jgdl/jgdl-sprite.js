function JGDLSprite() {
  this.position = new JGDLVector(0, 0);
  this.pr_Image = null;
  this.pr_Main = null;
  this.pr_Layer = null;
  this.bVisible = true;
  this.bFreezed = false;
  this.window = new JGDLVector(0, 0);
  this.Animations = [];
  this.iCurrentAnim = -1;
  this._moveFrom = new JGDLVector(0, 0);
  this._moveTo = new JGDLVector(0, 0);
  this._moveTime = new JGDLTimeAccumulator();
}

JGDLSprite.prototype.AddAnimation = function (fps, repeat, frameIndices) {
  var anim = new JGDLAnimation();
  anim.pr_Main = this.pr_Main;
  anim.iFramesPerSecond = fps;
  anim.bRepeat = repeat;
  anim.Frames = frameIndices ? frameIndices.slice() : [];
  this.Animations.push(anim);
};

JGDLSprite.prototype.SetCurrentAnimation = function (index) {
  if (index !== this.iCurrentAnim) {
    this.iCurrentAnim = index;
    if (index >= 0 && index < this.Animations.length) this.Animations[index].Reset();
  }
};

JGDLSprite.prototype.GetCurrentFrame = function () {
  if (this.iCurrentAnim < 0 || this.iCurrentAnim >= this.Animations.length) return 0;
  return this.Animations[this.iCurrentAnim].GetCurrentFrame();
};

JGDLSprite.prototype.Draw = function () {
  if (!this.pr_Main || !this.pr_Image || !this.pr_Image.image) return;
  var frameIndex = 0;
  if (this.iCurrentAnim >= 0 && this.iCurrentAnim < this.Animations.length) {
    frameIndex = this.Animations[this.iCurrentAnim].GetCurrentFrame();
    if (frameIndex < 0 && this.pr_Image.Frames.length) frameIndex = 0;
  }
  if (frameIndex >= 0 && this.pr_Image.Frames && this.pr_Image.Frames[frameIndex]) {
    this.pr_Main.VideoManager.DrawImage(this.position, this.pr_Image, frameIndex, 0);
  } else if (this.pr_Image.Frames && this.pr_Image.Frames[0]) {
    this.pr_Main.VideoManager.DrawImage(this.position, this.pr_Image, 0, 0);
  }
};

JGDLSprite.prototype.Update = function () {
  if (this.iCurrentAnim >= 0 && this.iCurrentAnim < this.Animations.length) {
    this.Animations[this.iCurrentAnim].Update();
  }
  if (!this._moveTime.Ended()) {
    this._moveTime.Update();
    var t = this._moveTime.iTimeAccum / this._moveTime.iTimeLimit;
    if (t >= 1) {
      this.position.fx = this._moveTo.fx;
      this.position.fy = this._moveTo.fy;
    } else {
      this.position.fx = this._moveFrom.fx + (this._moveTo.fx - this._moveFrom.fx) * t;
      this.position.fy = this._moveFrom.fy + (this._moveTo.fy - this._moveFrom.fy) * t;
    }
  }
};

JGDLSprite.prototype.MoveTo = function (x, y, ms) {
  this._moveFrom.fx = this.position.fx;
  this._moveFrom.fy = this.position.fy;
  this._moveTo.fx = typeof x === 'object' ? x.fx : x;
  this._moveTo.fy = typeof y === 'object' ? y.fy : (typeof y === 'number' ? y : this._moveTo.fy);
  this._moveTime.Init(this.pr_Main, ms || 100);
};

JGDLSprite.prototype.SetCenterPos = function (v) {
  var fw = this.pr_Image && this.pr_Image.FrameSize ? this.pr_Image.FrameSize.fx : this.window.fx;
  var fh = this.pr_Image && this.pr_Image.FrameSize ? this.pr_Image.FrameSize.fy : this.window.fy;
  this.position.fx = v.fx - fw * 0.5;
  this.position.fy = v.fy - fh * 0.5;
};

JGDLSprite.prototype.GetCenterPos = function (vOut) {
  var fw = this.pr_Image && this.pr_Image.FrameSize ? this.pr_Image.FrameSize.fx : this.window.fx;
  var fh = this.pr_Image && this.pr_Image.FrameSize ? this.pr_Image.FrameSize.fy : this.window.fy;
  vOut.fx = this.position.fx + fw * 0.5;
  vOut.fy = this.position.fy + fh * 0.5;
};

JGDLSprite.prototype.IsMouseOver = function () {
  var mp = this.pr_Main.InputManager.GetMousePos();
  var right = this.position.fx + (this.window.fx || (this.pr_Image && this.pr_Image.FrameSize ? this.pr_Image.FrameSize.fx : 0));
  var bottom = this.position.fy + (this.window.fy || (this.pr_Image && this.pr_Image.FrameSize ? this.pr_Image.FrameSize.fy : 0));
  return this.position.fx <= mp.fx && mp.fx <= right && this.position.fy <= mp.fy && mp.fy <= bottom;
};

JGDLSprite.prototype.Clicked = function (btn) {
  return this.IsMouseOver() && this.pr_Main.InputManager.MouBtnPressed(btn);
};

JGDLSprite.prototype.EndedAnimation = function () {
  if (this.iCurrentAnim < 0 || this.iCurrentAnim >= this.Animations.length) return false;
  return this.Animations[this.iCurrentAnim].Ended();
};

JGDLSprite.prototype.Release = function () {
  this.pr_Image = null;
  this.Animations = [];
};
