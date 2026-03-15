const WPA_SURPRISE = 0, WPA_STOPED1 = 1, WPA_STOPED2 = 2, WPA_STOPED3 = 3;
const WPA_FALLEDONTOP = 4, WPA_FALLED1 = 5, WPA_FALLED2 = 6, WPA_SAD = 7;
const WPA_FALLING = 28, WPA_FALLINGONTOP = 29, WPA_BURNING1 = 13, WPA_BURNING2 = 14;
const WPE_NONE = 0, WPE_EXPLODE = 1;
const WPU_NONE = 0, WPU_BOMB = 1, WPU_LINEREMOVE = 2;

function createPiece(id, board, level) {
  return {
    id,
    board: board || null,
    level: level || null,
    x: 0, y: 0,
    index: 0,
    falling: true,
    visited: false,
    fear: false,
    shake: false,
    powerUpType: WPU_NONE,
    effect: WPE_NONE,
    explodeY: 0,
    explodeVy: 0,
    explodeVx: 0,
    playFall: true,
    animTimer: 0,
    animEnd: 0,
  };
}

function pieceSameId(a, b) {
  return a && b && a.id === b.id;
}

function pieceClone(p, freeList) {
  const c = freeList.length ? freeList.pop() : createPiece(p.id, p.board, p.level);
  c.id = p.id;
  c.board = p.board;
  c.level = p.level;
  c.x = p.x;
  c.y = p.y;
  c.index = 0;
  c.falling = true;
  c.visited = false;
  c.fear = false;
  c.shake = false;
  c.powerUpType = p.powerUpType;
  c.effect = WPE_NONE;
  c.playFall = true;
  return c;
}

function pieceExploded(p) {
  return p.effect === WPE_EXPLODE && p.explodeY >= 350;
}

function pieceExplode(p, rand) {
  if (p.effect !== WPE_NONE) return;
  p.effect = WPE_EXPLODE;
  p.explodeY = p.y + p.board.posY;
  p.explodeVx = (rand() % 2 === 0 ? 1 : -1) * (35 + rand() % 70);
  p.explodeVy = -(210 + rand() % 70);
}
