const HELLO_SURFACES = 'assets/hello/surfaces/';
const HELLO_SOUNDS = 'assets/hello/sounds/';

const HELLO_IMAGE_NAMES = [
  'bkg_Interface.gif', 'spr_phone1.gif', 'spr_phone2.gif', 'spr_phone3.gif', 'spr_phone4.gif',
  'spr_phone1_end.gif', 'spr_phone2_end.gif', 'spr_phone3_end.gif', 'spr_phone4_end.gif',
  'spr_NextLine.gif', 'spr_Selection.gif', 'spr_LevelBar.gif',
  'spr_wire1.gif', 'spr_wire2.gif', 'spr_wire3.gif', 'spr_wire4.gif',
  'fnt_ButtonsBlue.gif', 'RingoStopped.gif',
];

// Sounds to load; .au files in assets/hello/sounds/ (e.g. trk_Level.au). sfx_* may be missing; fallback tones will play.
const HELLO_SOUND_NAMES = ['trk_Level.au'];

async function loadHelloAssets() {
  await AssetLoader.loadImages(HELLO_SURFACES, HELLO_IMAGE_NAMES);
  return {
    images: AssetLoader.images,
    basePath: HELLO_SURFACES,
  };
}

function drawHelloImage(ctx, assets, name, dx, dy, dw, dh, frameIndex) {
  const path = assets.basePath + name;
  const img = assets.images[path] || AssetLoader.getImage(path);
  if (!img) return;
  const fw = dw || 31;
  const fh = dh || 31;
  const sx = frameIndex != null ? (frameIndex * fw) % img.width : 0;
  const sy = frameIndex != null ? Math.floor((frameIndex * fw) / img.width) * fh : 0;
  ctx.drawImage(img, Math.min(sx, img.width - fw), Math.min(sy, img.height - fh), fw, fh, dx, dy, dw || fw, dh || fh);
}
