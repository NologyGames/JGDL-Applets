function createEffects(ctx) {
  const effects = [];
  const MAX = 200;

  function addEffect(type, x, y, vx, vy) {
    if (effects.length >= MAX) return;
    effects.push({
      type,
      x, y, vx, vy,
      t: 0,
      maxT: type === 'popcorn' ? 1.2 : 0.5,
      frame: Math.floor(Math.random() * 5),
    });
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
    draw() {
      effects.forEach(e => {
        if (e.type === 'popcorn') {
          ctx.fillStyle = '#f5deb3';
          ctx.beginPath();
          ctx.arc(e.x, e.y, 4, 0, Math.PI * 2);
          ctx.fill();
        } else {
          const s = 1 - e.t / e.maxT;
          ctx.fillStyle = `rgba(255, 200, 50, ${s})`;
          ctx.beginPath();
          ctx.arc(e.x, e.y, 8 * s, 0, Math.PI * 2);
          ctx.fill();
        }
      });
    },
  };
}
