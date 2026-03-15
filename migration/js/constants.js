const CONST = {
  CANVAS_WIDTH: 448,
  CANVAS_HEIGHT: 336,
  MAT_X: 7,
  MAT_Y: 11,
  MAT_SIZE: 77,
  PIECE_SIZE: 17,
  BOARD1_X: 21,
  BOARD2_X: 308,
  BOARD_Y: 77,
  BALANCE_LIMIT: 158,
  FALL_SPEED: 175,
  MOVE_SPEED: 140,
  FLEX_BASE: 3.5,
  FLEX_PER_LEVEL: 0.23,
  WSPEED_LIMIT: 90,
  REMOVE_DELAY_MS: 80,
  BOMB_RADIUS: 35,
  LEVELS: [
    { cornsToFind: 150, newLineMs: 6500, flexibility: 1.0 },
    { cornsToFind: 160, newLineMs: 6500, flexibility: 1.0 },
    { cornsToFind: 170, newLineMs: 6000, flexibility: 1.0, pu: { first: 4, every: 6, type: 'line' } },
    { cornsToFind: 180, newLineMs: 6000, flexibility: 1.3, pu: { first: 4, every: 6, type: 'line' } },
    { cornsToFind: 200, newLineMs: 5500, flexibility: 1.3, pu: { first: 4, every: 6, type: 'bomb' } },
    { cornsToFind: 220, newLineMs: 5000, flexibility: 1.3, pu: { first: 4, every: 6, type: 'bomb' } },
    { cornsToFind: 240, newLineMs: 5000, flexibility: 2.0, pu: { first: 4, every: 6, type: 'line' }, pu2: { first: 7, every: 6, type: 'bomb' } },
    { cornsToFind: 260, newLineMs: 4500, flexibility: 2.0, pu: { first: 7, every: 6, type: 'line' }, pu2: { first: 4, every: 6, type: 'bomb' } },
    { cornsToFind: 280, newLineMs: 4500, flexibility: 2.0, pu: { first: 4, every: 6, type: 'line' }, pu2: { first: 7, every: 6, type: 'bomb' } },
    { cornsToFind: 300, newLineMs: 4000, flexibility: 2.0, pu: { first: 7, every: 6, type: 'line' }, pu2: { first: 4, every: 6, type: 'bomb' } },
  ],
  COLORS: [
    '#e53935',   // red
    '#2e7d32',   // dark green
    '#1565c0',   // blue
    '#f9a825',   // yellow
  ],
  PIECE_IDS: { RED: 1, GREEN: 2, BLUE: 3, YELLOW: 4 },
  PU_TYPES: { NONE: 0, BOMB: 1, LINE: 2 },
};
