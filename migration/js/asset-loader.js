const AssetLoader = {
  images: {},
  audioBuffers: {},

  loadImage(path) {
    if (this.images[path]) return Promise.resolve(this.images[path]);
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.onload = () => {
        this.images[path] = img;
        resolve(img);
      };
      img.onerror = () => reject(new Error('Failed to load ' + path));
      img.src = path;
    });
  },

  loadImages(basePath, names) {
    return Promise.all(names.map(name => this.loadImage(basePath + name)));
  },

  getImage(path) {
    return this.images[path] || null;
  },

  async loadAuAsBuffer(url) {
    const res = await fetch(url);
    const arrayBuffer = await res.arrayBuffer();
    return arrayBuffer;
  },

  mulawDecode(byte) {
    const MULAW_BIAS = 33;
    const MULAW_MAX = 32635;
    byte = ~byte & 0xff;
    const sign = (byte & 0x80) ? -1 : 1;
    const exponent = (byte >> 4) & 0x07;
    const mantissa = byte & 0x0f;
    const sample = sign * ((((mantissa << 3) + MULAW_BIAS) << exponent) - MULAW_BIAS);
    return sample / MULAW_MAX;
  },

  decodeAu(arrayBuffer, audioCtx) {
    const view = new DataView(arrayBuffer);
    if (view.getUint32(0) !== 0x2E736E64) return null;
    const dataOffset = view.getUint32(4, false);
    const dataSize = view.getUint32(8, false);
    const encoding = view.getUint32(12, false);
    const sampleRate = view.getUint32(16, false);
    const channels = view.getUint32(20, false);
    const bytes = new Uint8Array(arrayBuffer, dataOffset, dataSize);
    let samples;
    if (encoding === 1) {
      samples = new Float32Array(bytes.length);
      for (let i = 0; i < bytes.length; i++) samples[i] = this.mulawDecode(bytes[i]);
    } else if (encoding === 2) {
      samples = new Float32Array(bytes.length);
      for (let i = 0; i < bytes.length; i++) samples[i] = (bytes[i] - 128) / 128;
    } else if (encoding === 3) {
      const len = bytes.length / 2;
      samples = new Float32Array(len);
      for (let i = 0; i < len; i++)
        samples[i] = view.getInt16(dataOffset + i * 2, false) / 32768;
    } else {
      return null;
    }
    const buffer = audioCtx.createBuffer(channels || 1, samples.length / (channels || 1), sampleRate);
    buffer.getChannelData(0).set(samples);
    return buffer;
  },

  async loadSound(basePath, name, audioCtx) {
    const key = basePath + name;
    if (this.audioBuffers[key]) return this.audioBuffers[key];
    const arrayBuffer = await this.loadAuAsBuffer(basePath + name);
    const buffer = this.decodeAu(arrayBuffer, audioCtx);
    if (buffer) this.audioBuffers[key] = buffer;
    return buffer;
  },

  playSound(buffer, audioCtx, volume) {
    if (!buffer || !audioCtx) return;
    const src = audioCtx.createBufferSource();
    src.buffer = buffer;
    const gain = audioCtx.createGain();
    gain.gain.value = volume != null ? volume : 0.3;
    src.connect(gain);
    gain.connect(audioCtx.destination);
    src.start(0);
  },
};
