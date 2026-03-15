function JGDLInputManager(canvas) {
  this.pr_Main = null;
  this.canvas = canvas;
  this.MousePos = new JGDLVector(0, 0);
  this.MouseState = [0, 0];
  this.LastMouseState = [0, 0];
  this.KeyboardState = {};
  this.LastKeyboardState = {};
  var self = this;
  if (canvas) {
    canvas.addEventListener('mousemove', function (e) {
      var rect = canvas.getBoundingClientRect();
      var scaleX = canvas.width / rect.width;
      var scaleY = canvas.height / rect.height;
      self.MousePos.fx = (e.clientX - rect.left) * scaleX;
      self.MousePos.fy = (e.clientY - rect.top) * scaleY;
    });
    canvas.addEventListener('mousedown', function (e) {
      var i = e.button === 2 ? 1 : 0;
      self.MouseState[i] = 1;
    });
    canvas.addEventListener('mouseup', function (e) {
      var i = e.button === 2 ? 1 : 0;
      self.MouseState[i] = 0;
    });
    canvas.addEventListener('contextmenu', function (e) { e.preventDefault(); });
  }
}

JGDLInputManager.prototype.Read = function () {
  this.LastMouseState[0] = this.MouseState[0];
  this.LastMouseState[1] = this.MouseState[1];
  for (var k in this.KeyboardState) this.LastKeyboardState[k] = this.KeyboardState[k];
};

JGDLInputManager.prototype.GetMousePos = function () {
  return this.MousePos;
};

JGDLInputManager.prototype.MouBtnPressed = function (btn) {
  return this.MouseState[btn] === 1 && this.LastMouseState[btn] === 0;
};

JGDLInputManager.prototype.MouBtnDown = function (btn) {
  return this.MouseState[btn] === 1;
};

JGDLInputManager.prototype.KeyPressed = function (code) {
  return this.KeyboardState[code] && !this.LastKeyboardState[code];
};

JGDLInputManager.prototype.KeyDown = function (code) {
  return !!this.KeyboardState[code];
};
