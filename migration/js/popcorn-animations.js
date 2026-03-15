/**
 * SPM (Super Popcorn Machine) sprite animations - from original PopLevel.java / PopPiece.java
 * Frame indices reference sprite sheets (e.g. 17x17 for corn, 14x21 for fire).
 */
(function (global) {
  'use strict';

  // --- Stove flames (spr_StoveFire.gif 14x21): 18 sprites, 3 animations ---
  // Original AddAnimation(iFPSParam,...) = FPS; frame interval = 1000/FPS ms
  const FIRE_ANIM1 = [0, 1, 2, 3, 4, 5, 6, 7];
  const FIRE_ANIM2 = [7, 6, 5, 4, 3, 2, 1, 0, -1]; // -1 = end
  const FIRE_ANIM3 = [5, 6, 7]; // loop
  const FLAME_FRAME_MS = [60, 126, 60]; // 50% slower
  const FLAME_ANIMS = [FIRE_ANIM1, FIRE_ANIM3, FIRE_ANIM2];

  // Flame positions (original: 9 at 13+i*14.7, 313 and 9 at 302+(i-9)*14.7, 313)
  const FLAME_POSITIONS = [];
  for (let i = 0; i < 9; i++) {
    FLAME_POSITIONS.push({ x: 13 + i * 14.7, y: 313 });
  }
  for (let i = 0; i < 9; i++) {
    FLAME_POSITIONS.push({ x: 302 + i * 14.7, y: 313 });
  }

  // --- Shake lever (spr_alavanca.gif): 9 frames, original 20 FPS = 50ms per frame ---
  const SHAKE_FRAMES = [0, 2, 4, 6, 8, 1, 3, 5, 7];
  const SHAKE_FRAME_MS = 100; // 50% slower

  // Board 2 uses reverse order
  const SHAKE_FRAMES_BOARD1 = [0, 2, 4, 6, 8, 1, 3, 5, 7];
  const SHAKE_FRAMES_BOARD2 = [0, 7, 5, 3, 1, 8, 6, 4, 2];

  // --- Piece animations: original AddAnimation(iFPSParam,...) = FPS; frame interval = 1000/FPS ms ---
  const PIECE_ANIMS = {
    WPA_SURPRISE:    { ms: 134,  loop: true,  frames: [0,1,2,3,4,5,6,7,6,6] },
    WPA_STOPED1:     { ms: 400,  loop: true,  frames: [40,40,40,7,40,40,40,8,8,40] },
    WPA_STOPED2:     { ms: 400,  loop: true,  frames: [39,39,37,37,37,37,30,37,37,37,39,39] },
    WPA_STOPED3:     { ms: 400,  loop: true,  frames: [37,37,30,37,37,37,37,37,37,37,37,37] },
    WPA_FALLEDONTOP: { ms: 182,  loop: false, frames: [17,28,28] },
    WPA_FALLED1:     { ms: 182,  loop: false, frames: [21,22,23,24,25,29,29,29,26,27,27,29,29,37,37] },
    WPA_FALLED2:     { ms: 166,  loop: false, frames: [21,22,23,24,25,28,28,28,26,26,27,27,26,29,29] },
    WPA_SAD:         { ms: 334,  loop: true,  frames: [16,16,17,17,17,85,85,82,85,16,16,16] },
    WPA_HEATDROPS1:  { ms: 500,  loop: true,  frames: [58,59,60,61,59] },
    WPA_HEATDROPS2:  { ms: 500,  loop: true,  frames: [60,58,62] },
    WPA_FELLINGHOT1: { ms: 500,  loop: true,  frames: [63,64,65,66,67,68,69] },
    WPA_FELLINGHOT2: { ms: 500,  loop: true,  frames: [68,69,70,71,72] },
    WPA_FELLINGHOT3: { ms: 500,  loop: true,  frames: [72,72,73,74,74] },
    WPA_BURNING1:    { ms: 166,  loop: true,  frames: [76,77,78,79,79,80,78,78,78,78] },
    WPA_BURNING2:    { ms: 166,  loop: true,  frames: [76,77,78,79,80,78,78,80,78,78] },
    WPA_HAPPYLEFT:   { ms: 400,  loop: true,  frames: [36,40,42,43,33,32,32,31,31,35,31] },
    WPA_HAPPYRIGHT:  { ms: 400,  loop: true,  frames: [36,40,47,48,34,32,32,31,31,35,31] },
    WPA_BLINKLEFT:   { ms: 400,  loop: true,  frames: [40,39,40,42,43,44,43,43,43,40,40] },
    WPA_BLINKRIGHT:  { ms: 400,  loop: true,  frames: [40,39,40,47,48,49,48,48,48,40,40] },
    WPA_BLINKKISSLEFT:  { ms: 400, loop: true, frames: [40,41,40,42,43,44,43,43,43,45,45,43,43,43] },
    WPA_BLINKKISSRIGHT: { ms: 400, loop: true, frames: [40,41,40,47,48,49,48,48,48,50,50,48,48,48] },
    WPA_TONGUERIGHT: { ms: 400,  loop: true,  frames: [39,9,9,10,10,10,11,11,10,10,10,16,16,16] },
    WPA_TONGUELEFT:  { ms: 400,  loop: true,  frames: [39,13,13,14,14,14,15,15,14,14,14,16,16,16] },
    WPA_FLYLEFT:     { ms: 166,  loop: true,  frames: [54,55] },
    WPA_FLYRIGHT:    { ms: 166,  loop: true,  frames: [53,52] },
    WPA_ANGRY1:      { ms: 400,  loop: true,  frames: [37,37,37,30,37,29,29,19,88,88,26,26,28,28,28,17,17,29,29,19] },
    WPA_ANGRY2:      { ms: 400,  loop: true,  frames: [37,37,37,12,12,12,88,88,88,12] },
    WPA_SMOKE1:      { ms: 400,  loop: true,  frames: [78,78,78,80,78,78] },
    WPA_FALLING:     { ms: 142,  loop: false, frames: [17,19,20] },
    WPA_FALLINGONTOP: { ms: 200, loop: false, frames: [28] },
  };

  const WPA_SURPRISE = 0, WPA_STOPED1 = 1, WPA_STOPED2 = 2, WPA_STOPED3 = 3;
  const WPA_FALLEDONTOP = 4, WPA_FALLED1 = 5, WPA_FALLED2 = 6, WPA_SAD = 7;
  const WPA_FALLING = 28, WPA_FALLINGONTOP = 29, WPA_BURNING1 = 13, WPA_BURNING2 = 14;

  // --- Sensor: lay_sensorback 4 frames; inp_SensorDisplay 50 frames (0-24 or 25-49 for direction) ---
  const SENSOR_BACK_FRAMES = 4;
  const SENSOR_POINT_FRAMES = 50;

  // --- PopsBar: spr_PopsBar 80x16, 2 frames (0 = fill bar, 1 = bg), window.fx for progress ---
  const POPSBAR_FRAMES = 2;

  // --- Explosion: spr_Explosion 17x17, 17 frames (0-15, 16=-1 end). Original 20 FPS = 50ms ---
  const EXPLOSION_FRAMES = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,-1];
  const EXPLOSION_FRAME_MS = 100; // 50% slower

  // --- Popcorn effect: inp_PopCorn 35x35, 20 single-frame "animations" (frames 0-19) ---
  const POPCORN_FRAMES = 20;

  var SPM_ANIM = {
    FLAME_ANIMS: FLAME_ANIMS,
    FLAME_FRAME_MS: FLAME_FRAME_MS,
    FLAME_POSITIONS: FLAME_POSITIONS,
    SHAKE_FRAMES_BOARD1: SHAKE_FRAMES_BOARD1,
    SHAKE_FRAMES_BOARD2: SHAKE_FRAMES_BOARD2,
    SHAKE_FRAME_MS: SHAKE_FRAME_MS,
    PIECE_ANIMS: PIECE_ANIMS,
    WPA_SURPRISE: WPA_SURPRISE,
    WPA_STOPED1: WPA_STOPED1,
    WPA_STOPED2: WPA_STOPED2,
    WPA_STOPED3: WPA_STOPED3,
    WPA_FALLEDONTOP: WPA_FALLEDONTOP,
    WPA_FALLED1: WPA_FALLED1,
    WPA_FALLED2: WPA_FALLED2,
    WPA_SAD: WPA_SAD,
    WPA_FALLING: WPA_FALLING,
    WPA_FALLINGONTOP: WPA_FALLINGONTOP,
    WPA_BURNING1: WPA_BURNING1,
    WPA_BURNING2: WPA_BURNING2,
    SENSOR_BACK_FRAMES: SENSOR_BACK_FRAMES,
    SENSOR_POINT_FRAMES: SENSOR_POINT_FRAMES,
    POPSBAR_FRAMES: POPSBAR_FRAMES,
    EXPLOSION_FRAMES: EXPLOSION_FRAMES,
    EXPLOSION_FRAME_MS: EXPLOSION_FRAME_MS,
    POPCORN_FRAMES: POPCORN_FRAMES,
    getPieceAnimByIndex: function (wpaIndex) {
      var list = [];
      list[0] = PIECE_ANIMS.WPA_SURPRISE;
      list[1] = PIECE_ANIMS.WPA_STOPED1;
      list[2] = PIECE_ANIMS.WPA_STOPED2;
      list[3] = PIECE_ANIMS.WPA_STOPED3;
      list[4] = PIECE_ANIMS.WPA_FALLEDONTOP;
      list[5] = PIECE_ANIMS.WPA_FALLED1;
      list[6] = PIECE_ANIMS.WPA_FALLED2;
      list[7] = PIECE_ANIMS.WPA_SAD;
      list[13] = PIECE_ANIMS.WPA_BURNING1;
      list[14] = PIECE_ANIMS.WPA_BURNING2;
      list[15] = PIECE_ANIMS.WPA_HAPPYLEFT;
      list[16] = PIECE_ANIMS.WPA_HAPPYRIGHT;
      list[17] = PIECE_ANIMS.WPA_BLINKLEFT;
      list[18] = PIECE_ANIMS.WPA_BLINKRIGHT;
      list[19] = PIECE_ANIMS.WPA_BLINKKISSLEFT;
      list[20] = PIECE_ANIMS.WPA_BLINKKISSRIGHT;
      list[21] = PIECE_ANIMS.WPA_TONGUERIGHT;
      list[22] = PIECE_ANIMS.WPA_TONGUELEFT;
      list[23] = PIECE_ANIMS.WPA_FLYLEFT;
      list[24] = PIECE_ANIMS.WPA_FLYRIGHT;
      list[25] = PIECE_ANIMS.WPA_ANGRY1;
      list[26] = PIECE_ANIMS.WPA_ANGRY2;
      list[27] = PIECE_ANIMS.WPA_SMOKE1;
      list[28] = PIECE_ANIMS.WPA_FALLING;
      list[29] = PIECE_ANIMS.WPA_FALLINGONTOP;
      return list[wpaIndex] || PIECE_ANIMS.WPA_STOPED1;
    },
    getPieceFrameIndex: function (anim, timeMs) {
      if (!anim || !anim.frames || !anim.frames.length) return 0;
      const ms = anim.ms || 10;
      const total = anim.frames.length * ms;
      let t = timeMs % total;
      if (!anim.loop && timeMs >= total) {
        const last = anim.frames[anim.frames.length - 1];
        return last === -1 ? anim.frames[anim.frames.length - 2] : last;
      }
      let i = Math.floor(t / ms) % anim.frames.length;
      let f = anim.frames[i];
      if (f === -1) f = anim.frames[Math.max(0, i - 1)];
      return f;
    }
  };

  if (typeof module !== 'undefined' && module.exports) {
    module.exports = SPM_ANIM;
  } else {
    global.SPM_ANIM = SPM_ANIM;
  }
})(typeof window !== 'undefined' ? window : this);
