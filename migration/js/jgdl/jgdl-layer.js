function JGDLLayer() {
  this.pr_Scene = null;
  this.BrickSize = new JGDLVector(0, 0);
  this.Sprites = [];
  this.bVisible = true;
}

JGDLLayer.prototype.CreateSprite = function (fileName, frameSize) {
  var sprite = new JGDLSprite();
  sprite.pr_Layer = this;
  sprite.pr_Main = this.pr_Scene.pr_Main;
  var path = this.pr_Scene.pr_Main.ImagesDir + fileName;
  var img = this.pr_Scene.pr_Main.VideoManager.LoadImage(path);
  if (!img && this.pr_Scene.pr_Main._imageCache && this.pr_Scene.pr_Main._imageCache[path]) {
    img = this.pr_Scene.pr_Main.VideoManager.RegisterImage(path, this.pr_Scene.pr_Main._imageCache[path], frameSize);
  }
  sprite.pr_Image = img;
  sprite.window = new JGDLVector(frameSize.fx || frameSize[0], frameSize.fy || frameSize[1]);
  if (img && !img.Frames.length && frameSize) {
    var fw = frameSize.fx || frameSize[0];
    var fy = frameSize.fy || frameSize[1];
    var cols = Math.floor(img.image.width / fw);
    var rows = Math.floor(img.image.height / fy);
    for (var j = 0; j < rows; j++)
      for (var i = 0; i < cols; i++)
        img.Frames.push({ iLeft: i * fw, iTop: j * fy, iRight: (i + 1) * fw, iBottom: (j + 1) * fy });
  }
  this.Sprites.push(sprite);
  return sprite;
};

JGDLLayer.prototype.Draw = function () {
  for (var i = 0; i < this.Sprites.length; i++) {
    if (this.Sprites[i].bVisible) this.Sprites[i].Draw();
  }
};

JGDLLayer.prototype.Update = function () {
  for (var i = 0; i < this.Sprites.length; i++) {
    if (!this.Sprites[i].bFreezed) this.Sprites[i].Update();
  }
};

JGDLLayer.prototype.Release = function () {
  for (var i = this.Sprites.length - 1; i >= 0; i--) {
    this.Sprites[i].Release();
  }
  this.Sprites = [];
};
