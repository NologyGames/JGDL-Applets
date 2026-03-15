const MAT_X = 7, MAT_Y = 11, MAT_SIZE = 77, PIECE_SIZE = 17;

function createBoard(level, boardIndex) {
  const matrix = Array(MAT_SIZE).fill(null);
  const newPieces = Array(MAT_X).fill(null);
  const removeList = [];
  const deleteList = []; // { piece, time }
  let deleteSize = 0, removeSize = 0;
  let removeTimer = 0;
  let posX, posY;
  const side = boardIndex === 0 ? CONST.BOARD1_X : CONST.BOARD2_X;
  posX = side;
  posY = CONST.BOARD_Y;

  function index(i, j) {
    return i + j * MAT_X;
  }

  function clearVisited() {
    for (let i = 0; i < MAT_SIZE; i++)
      if (matrix[i]) matrix[i].visited = false;
  }

  function isInRemoveList(piece) {
    for (let i = 0; i < removeSize; i++) if (removeList[i] === piece) return true;
    for (let i = 0; i < deleteSize; i++) if (deleteList[i].piece === piece) return true;
    return false;
  }

  function addToRemoveList(piece) {
    if (!piece) return;
    for (let i = removeSize - 1; i >= 0; i--) if (removeList[i] === piece) return;
    for (let i = 0; i < deleteSize; i++) if (deleteList[i].piece === piece) return;
    removeList[removeSize++] = piece;
  }

  function deleteRecursive(mx, my) {
    const idx = index(mx, my);
    const p = matrix[idx];
    if (!p || p.visited) return;
    p.visited = true;
    if (mx + 1 < MAT_X && matrix[index(mx + 1, my)] && pieceSameId(matrix[index(mx + 1, my)], p))
      deleteRecursive(mx + 1, my);
    if (mx - 1 >= 0 && matrix[index(mx - 1, my)] && pieceSameId(matrix[index(mx - 1, my)], p))
      deleteRecursive(mx - 1, my);
    if (my + 1 < MAT_Y && matrix[index(mx, my + 1)] && pieceSameId(matrix[index(mx, my + 1)], p))
      deleteRecursive(mx, my + 1);
    if (my - 1 >= 0 && matrix[index(mx, my - 1)] && pieceSameId(matrix[index(mx, my - 1)], p))
      deleteRecursive(mx, my - 1);
    addToRemoveList(p);
  }

  function clearRemoveLists() {
    removeSize = 0;
    deleteSize = 0;
  }

  function deletePiece(piece, delayMs) {
    if (deleteSize >= 143 || isInRemoveList(piece)) return;
    deleteList[deleteSize] = { piece, time: delayMs };
    deleteSize++;
  }

  function createLine(getRandomPiece) {
    for (let col = 0; col < MAT_X; col++) {
      for (let row = MAT_Y - 1; row > 0; row--) {
        const idx = index(col, row);
        const above = index(col, row - 1);
        if (!matrix[idx] && matrix[above]) {
          matrix[idx] = matrix[above];
          matrix[above] = null;
        }
      }
      if (!matrix[index(col, 0)]) {
        if (newPieces[col]) {
          matrix[index(col, 0)] = newPieces[col];
          matrix[index(col, 0)].x = col * PIECE_SIZE;
          matrix[index(col, 0)].y = 0;
          matrix[index(col, 0)].index = index(col, 0);
          matrix[index(col, 0)].falling = true;
        }
        const np = getRandomPiece();
        np.x = col * PIECE_SIZE;
        np.y = -PIECE_SIZE;
        np.board = { posX: () => posX, posY: () => posY };
        np.falling = true;
        newPieces[col] = np;
      } else {
        return false;
      }
    }
    return true;
  }

  function handleFalling(dt) {
    const moveX = CONST.MOVE_SPEED * dt;
    const moveY = CONST.FALL_SPEED * dt;
    for (let col = 0; col < MAT_X; col++) {
      for (let row = MAT_Y - 1; row >= 0; row--) {
        const idx = index(col, row);
        const p = matrix[idx];
        if (!p) continue;
        p.index = idx;
        const targetX = col * PIECE_SIZE;
        const targetY = row * PIECE_SIZE;
        if (p.x < targetX) {
          p.x += moveX;
          if (p.x > targetX) p.x = targetX;
        } else if (p.x > targetX) {
          p.x -= moveX;
          if (p.x < targetX) p.x = targetX;
        }
        if (p.y < targetY) {
          p.y += moveY;
          p.falling = true;
          if (p.y >= targetY) {
            if (row < MAT_Y - 1 && !matrix[index(col, row + 1)]) {
              matrix[index(col, row + 1)] = p;
              p.index = index(col, row + 1);
              matrix[idx] = null;
            } else {
              p.y = targetY;
              p.falling = false;
            }
          }
        } else {
          if (row < MAT_Y - 1 && !matrix[index(col, row + 1)]) {
            matrix[index(col, row + 1)] = p;
            p.index = index(col, row + 1);
            matrix[idx] = null;
          } else {
            p.y = targetY;
            p.falling = false;
          }
        }
      }
    }
  }

  function updateRemoveList(dt, onPop, effects) {
    const dtMs = dt * 1000;
    for (let i = deleteSize - 1; i >= 0; i--) {
      deleteList[i].time -= dtMs;
      if (deleteList[i].time <= 0) {
        const p = deleteList[i].piece;
        const idx = p.index;
        if (matrix[idx] === p) {
          level.freePieces.push(matrix[idx]);
          matrix[idx] = null;
          level.points += (level.state === 'game') ? 31 : 1;
          level.foundCorns++;
        }
        deleteList[i] = deleteList[deleteSize - 1];
        deleteSize--;
      }
    }
    removeTimer += dtMs;
    while (removeTimer >= 80 && removeSize > 0) {
      removeTimer -= 80;
      const p = removeList[0];
      for (let i = 0; i < removeSize - 1; i++) removeList[i] = removeList[i + 1];
      removeList[removeSize - 1] = null;
      removeSize--;
      if (!p) continue;
      const idx = p.index;
      if (matrix[idx] === p) {
        onPop && onPop();
        const cx = posX + 8 + (p.index % MAT_X) * PIECE_SIZE;
        const cy = posY + 8 + Math.floor(p.index / MAT_X) * PIECE_SIZE;
        effects.createPopcorn(cx, cy);
        level.freePieces.push(matrix[idx]);
        matrix[idx] = null;
        level.points += (level.state === 'game') ? 31 : 1;
        level.foundCorns++;
      }
    }
  }

  function checkColumns() {
    const empty = [];
    for (let c = 0; c < MAT_X; c++) {
      let emptyCol = true;
      for (let r = 0; r < MAT_Y; r++) {
        if (matrix[index(c, r)]) {
          emptyCol = false;
          break;
        }
      }
      empty[c] = emptyCol;
    }
    for (let c = 3; c > 0; c--) {
      if (empty[c]) {
        for (let r = 0; r < MAT_Y; r++) {
          matrix[index(c, r)] = matrix[index(c - 1, r)];
          matrix[index(c - 1, r)] = null;
        }
      }
    }
    for (let c = 3; c < 6; c++) {
      if (empty[c]) {
        for (let r = 0; r < MAT_Y; r++) {
          matrix[index(c, r)] = matrix[index(c + 1, r)];
          matrix[index(c + 1, r)] = null;
        }
      }
    }
  }

  function countPieces() {
    let n = 0;
    for (let i = 0; i < MAT_SIZE; i++) if (matrix[i]) n++;
    return n;
  }

  function explodeAll() {
    clearRemoveLists();
    for (let i = 0; i < MAT_SIZE; i++)
      if (matrix[i]) pieceExplode(matrix[i], () => Math.abs(level.rand()));
  }

  function clear() {
    clearRemoveLists();
    for (let c = 0; c < MAT_X; c++) {
      if (newPieces[c]) {
        level.freePieces.push(newPieces[c]);
        newPieces[c] = null;
      }
    }
    for (let i = 0; i < MAT_SIZE; i++)
      if (matrix[i]) addToRemoveList(matrix[i]);
  }

  return {
    matrix,
    newPieces,
    posX: () => posX,
    posY: () => posY,
    setPos(x, y) {
      posX = x;
      posY = y;
    },
    createLine,
    handleFalling,
    updateRemoveList,
    checkColumns,
    countPieces,
    clearVisited,
    deleteRecursive,
    addToRemoveList,
    isInRemoveList,
    deletePiece,
    clear,
    explodeAll,
    get matrixRef() { return matrix; },
    get newPiecesRef() { return newPieces; },
  };
}
