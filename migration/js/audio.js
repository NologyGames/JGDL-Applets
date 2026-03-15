const AudioManager = {
  ctx: null,
  enabled: true,
  popcornSounds: null,
  helloSounds: null,

  init() {
    if (this.ctx) return;
    try {
      this.ctx = new (window.AudioContext || window.webkitAudioContext)();
    } catch (e) {}
  },

  setPopcornAssets(assets) {
    if (assets && (assets.SoundManager || (assets.sounds && assets.audioCtx))) {
      this.popcornSounds = assets;
      if (!this.ctx && assets.audioCtx) this.ctx = assets.audioCtx;
    } else {
      this.popcornSounds = null;
    }
  },

  setHelloAssets(assets) {
    if (assets && (assets.SoundManager || (assets.sounds && assets.audioCtx))) {
      this.helloSounds = assets;
      var ctx = assets.SoundManager && assets.SoundManager.audioCtx || assets.audioCtx;
      if (ctx) this.ctx = ctx;
    } else {
      this.helloSounds = null;
    }
  },

  playTone(freq, duration, type) {
    if (!this.enabled || !this.ctx) return;
    if (this.ctx.state === 'suspended') this.ctx.resume();
    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();
    osc.connect(gain);
    gain.connect(this.ctx.destination);
    osc.frequency.value = freq || 440;
    osc.type = type || 'square';
    gain.gain.setValueAtTime(0.1, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + (duration || 0.1));
    osc.start(this.ctx.currentTime);
    osc.stop(this.ctx.currentTime + (duration || 0.1));
  },

  pop() {
    this.init();
    const idx = 1 + Math.floor(Math.random() * 6);
    const name = 'sfx_PopCorn' + idx + '.au';
    if (this.popcornSounds && this.popcornSounds.SoundManager) {
      this.popcornSounds.SoundManager.Play((this.popcornSounds.SoundsDir || '') + name);
    } else if (this.popcornSounds && this.popcornSounds.sounds && this.popcornSounds.sounds[name]) {
      AssetLoader.playSound(this.popcornSounds.sounds[name], this.ctx, 0.25);
    } else {
      this.playTone(300 + Math.random() * 200, 0.05, 'square');
    }
  },

  popGround() {
    this.init();
    if (this.popcornSounds && this.popcornSounds.SoundManager) {
      this.popcornSounds.SoundManager.Play((this.popcornSounds.SoundsDir || '') + 'sfx_PopGround.au');
    } else if (this.popcornSounds && this.popcornSounds.sounds && this.popcornSounds.sounds['sfx_PopGround.au']) {
      AssetLoader.playSound(this.popcornSounds.sounds['sfx_PopGround.au'], this.ctx, 0.25);
    } else {
      this.playTone(150, 0.08, 'square');
    }
  },

  menuMove() {
    this.init();
    if (this.popcornSounds && this.popcornSounds.SoundManager) {
      this.popcornSounds.SoundManager.Play((this.popcornSounds.SoundsDir || '') + 'sfx_MenuMove.au');
    } else if (this.popcornSounds && this.popcornSounds.sounds && this.popcornSounds.sounds['sfx_MenuMove.au']) {
      AssetLoader.playSound(this.popcornSounds.sounds['sfx_MenuMove.au'], this.ctx, 0.25);
    } else {
      this.playTone(400, 0.06, 'square');
    }
  },

  bomb() {
    this.init();
    if (this.popcornSounds && this.popcornSounds.SoundManager) {
      this.popcornSounds.SoundManager.Play((this.popcornSounds.SoundsDir || '') + 'sfx_PUBomb.au');
    } else if (this.popcornSounds && this.popcornSounds.sounds && this.popcornSounds.sounds['sfx_PUBomb.au']) {
      AssetLoader.playSound(this.popcornSounds.sounds['sfx_PUBomb.au'], this.ctx, 0.3);
    } else {
      this.playTone(80, 0.15, 'sawtooth');
    }
  },

  lineRemove() {
    this.init();
    if (this.popcornSounds && this.popcornSounds.SoundManager) {
      this.popcornSounds.SoundManager.Play((this.popcornSounds.SoundsDir || '') + 'sfx_PULine.au');
    } else if (this.popcornSounds && this.popcornSounds.sounds && this.popcornSounds.sounds['sfx_PULine.au']) {
      AssetLoader.playSound(this.popcornSounds.sounds['sfx_PULine.au'], this.ctx, 0.25);
    } else {
      this.playTone(200, 0.1, 'square');
    }
  },

  toggle() {
    this.enabled = !this.enabled;
    return this.enabled;
  },

  // Hello! game sounds (phones: connect by color). Use tone fallback when .au not loaded.
  helloClickPhone() {
    this.init();
    const played = this.helloSounds && this.helloSounds.SoundManager &&
      this.helloSounds.SoundManager.Play((this.helloSounds.SoundsDir || '') + 'sfx_ClickPhone.au');
    if (!played) this.playTone(600, 0.04, 'sine');
  },
  helloNewPhones() {
    this.init();
    const played = this.helloSounds && this.helloSounds.SoundManager &&
      this.helloSounds.SoundManager.Play((this.helloSounds.SoundsDir || '') + 'sfx_NewPhones.au');
    if (!played) this.playTone(440, 0.06, 'sine');
  },
  helloPhoneRemoved() {
    this.init();
    const tones = ['sfx_Tone1.au', 'sfx_Tone2.au', 'sfx_Tone3.au', 'sfx_Tone4.au', 'sfx_Tone5.au', 'sfx_Tone6.au', 'sfx_Tone7.au'];
    const name = tones[Math.floor(Math.random() * tones.length)];
    const played = this.helloSounds && this.helloSounds.SoundManager &&
      this.helloSounds.SoundManager.Play((this.helloSounds.SoundsDir || '') + name);
    if (!played) this.playTone(300 + Math.random() * 200, 0.05, 'square');
  },
};
