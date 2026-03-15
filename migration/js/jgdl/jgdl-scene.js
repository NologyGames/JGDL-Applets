function JGDLScene() {
  this.pr_Main = null;
  this.Layers = [];
}

JGDLScene.prototype.CreateLayer = function (size) {
  var layer = new JGDLLayer();
  layer.BrickSize = size;
  layer.pr_Scene = this;
  this.Layers.push(layer);
  return layer;
};

JGDLScene.prototype.Draw = function () {
  for (var i = 0; i < this.Layers.length; i++) {
    if (this.Layers[i].bVisible) this.Layers[i].Draw();
  }
};

JGDLScene.prototype.Update = function () {
  for (var i = 0; i < this.Layers.length; i++) {
    this.Layers[i].Update();
  }
};

JGDLScene.prototype.Initialize = function () {
  return true;
};

JGDLScene.prototype.Execute = function () {};

JGDLScene.prototype.Release = function () {
  for (var i = this.Layers.length - 1; i >= 0; i--) {
    this.Layers[i].Release();
  }
  this.Layers = [];
};
