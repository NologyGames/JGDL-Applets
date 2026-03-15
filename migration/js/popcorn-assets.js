const POPCORN_SURFACES = 'assets/popcorn/surfaces/';
const POPCORN_SOUNDS = 'assets/popcorn/sounds/';

const POPCORN_IMAGE_NAMES = [
  'bkg_Movies.gif', 'spr_pan.gif', 'spr_Stove.gif', 'spr_StoveFire.gif',
  'inp_RedCorn.gif', 'inp_DarkGreenCorn.gif', 'inp_BlueCorn.gif', 'inp_YellowCorn.gif',
  'inp_PopCorn.gif', 'spr_Explosion.gif', 'spr_MainScreen.gif',
  'lay_sensorback.gif', 'inp_SensorDisplay.gif', 'spr_PopsBar.gif',
  'lay_Info.gif', 'spr_colorlights.gif', 'spr_alavanca.gif',
  'spr_BkgMenu.gif', 'btn_MenuTag.gif', 'btn_menubuttons.gif',
  'men_PopUp.gif', 'men_PopUpTitle.gif', 'spr_Congrats.gif',
];

const POPCORN_SOUND_NAMES = [
  'sfx_PopCorn1.au', 'sfx_PopCorn2.au', 'sfx_PopCorn3.au',
  'sfx_PopCorn4.au', 'sfx_PopCorn5.au', 'sfx_PopCorn6.au',
  'sfx_PopGround.au', 'sfx_MenuMove.au', 'sfx_PUBomb.au', 'sfx_PULine.au',
];

async function loadPopcornAssets() {
  const audioCtx = typeof AudioContext !== 'undefined' ? new AudioContext() : null;
  await AssetLoader.loadImages(POPCORN_SURFACES, POPCORN_IMAGE_NAMES);
  const sounds = {};
  if (audioCtx) {
    for (const name of POPCORN_SOUND_NAMES) {
      try {
        const buf = await AssetLoader.loadSound(POPCORN_SOUNDS, name, audioCtx);
        if (buf) sounds[name] = buf;
      } catch (e) {}
    }
  }
  return {
    images: AssetLoader.images,
    sounds,
    audioCtx,
    basePath: POPCORN_SURFACES,
    soundPath: POPCORN_SOUNDS,
  };
}

function drawPopcornImage(ctx, assets, name, dx, dy, dw, dh, frameIndex) {
  const img = assets.images[assets.basePath + name] || AssetLoader.getImage(assets.basePath + name);
  if (!img) return;
  const fw = dw || img.width;
  const fh = dh || img.height;
  const sx = frameIndex != null ? (frameIndex * fw) % img.width : 0;
  const sy = frameIndex != null ? Math.floor((frameIndex * fw) / img.width) * fh : 0;
  ctx.drawImage(img, sx, sy, fw, fh, dx, dy, dw || fw, dh || fh);
}
