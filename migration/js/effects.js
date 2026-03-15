function createEffects(ctx) {
  const effects = [];
  const MAX = 200;
  const EXPLOSION_FRAME_MS = 0.1;   // 100ms per frame (50% slower)
  const EXPLOSION_FRAMES = 16;

  function addEffect(type, x, y, vx, vy) {
    if (effects.length >= MAX) return;
    effects.push({
      type,
      x, y, vx, vy,
      t: 0,
      maxT: type === 'popcorn' ? 1.2 : 1.7,
      frame: type === 'popcorn' ? Math.floor(Math.random() * 20) : 0,
    });
  }

  function drawSpriteFrame(ctx, img, frameIndex, cellW, cellH, dx, dy, dw, dh) {
    if (!img || frameIndex < 0) return;
    var cols = Math.max(1, Math.floor(img.width / cellW));
    var sx = (frameIndex % cols) * cellW;
    var sy = Math.floor(frameIndex / cols) * cellH;
    ctx.drawImage(img, sx, sy, cellW, cellH, dx, dy, dw || cellW, dh || cellH);
  }

  return {
    createPopcorn(x, y) {
      addEffect('popcorn', x, y, (Math.random() - 0.5) * 200, -150 - Math.random() * 200);
    },
    createExplosion(x, y) {
      addEffect('explosion', x, y, 0, 0);
    },
    update(dt) {
      const g = 800;
      for (let i = effects.length - 1; i >= 0; i--) {
        const e = effects[i];
        e.t += dt;
        if (e.t >= e.maxT) {
          effects.splice(i, 1);
          continue;
        }
        if (e.type === 'popcorn') {
          e.vy += g * dt;
          e.x += e.vx * dt;
          e.y += e.vy * dt;
        }
      }
    },
    draw(getImg) {
      effects.forEach(e => {
        if (e.type === 'popcorn') {
          var img = getImg && getImg('inp_PopCorn.gif');
          if (img) {
            var f = Math.min(e.frame, 19);
            drawSpriteFrame(ctx, img, f, 35, 35, e.x - 17, e.y - 17, 35, 35);
          } else {
            ctx.fillStyle = '#f5deb3';
            ctx.beginPath();
            ctx.arc(e.x, e.y, 4, 0, Math.PI * 2);
            ctx.fill();
          }
        } else {
          var img = getImg && getImg('spr_Explosion.gif');
          if (img) {
            var frame = Math.min(EXPLOSION_FRAMES, Math.floor(e.t / EXPLOSION_FRAME_MS));
            drawSpriteFrame(ctx, img, frame, 17, 17, e.x - 8, e.y - 8, 17, 17);
          } else {
            const s = 1 - e.t / e.maxT;
            ctx.fillStyle = `rgba(255, 200, 50, ${s})`;
            ctx.beginPath();
            ctx.arc(e.x, e.y, 8 * s, 0, Math.PI * 2);
            ctx.fill();
          }
        }
      });
    },
  };
}
