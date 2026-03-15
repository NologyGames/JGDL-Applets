function JGDLSoundManager() {
  this.pr_Main = null;
  this.bEnableSounds = true;
  this.Sounds = {};
  this.audioCtx = null;
}

JGDLSoundManager.prototype.Initialize = function () {
  try {
    this.audioCtx = new (window.AudioContext || window.webkitAudioContext)();
  } catch (e) {}
};

JGDLSoundManager.prototype.LoadSound = function (path, arrayBuffer, decodedBuffer) {
  if (this.Sounds[path]) return this.Sounds[path];
  if (decodedBuffer) {
    this.Sounds[path] = { buffer: decodedBuffer };
    return this.Sounds[path];
  }
  return null;
};

JGDLSoundManager.prototype.RegisterSound = function (path, buffer) {
  this.Sounds[path] = { buffer: buffer };
  return this.Sounds[path];
};

JGDLSoundManager.prototype.Play = function (path) {
  if (!this.bEnableSounds || !this.audioCtx) return false;
  var s = this.Sounds[path];
  if (!s || !s.buffer) return false;
  var src = this.audioCtx.createBufferSource();
  src.buffer = s.buffer;
  var gain = this.audioCtx.createGain();
  gain.gain.value = 0.25;
  src.connect(gain);
  gain.connect(this.audioCtx.destination);
  src.start(0);
  return true;
};

JGDLSoundManager.prototype.Release = function () {
  this.Sounds = {};
};
