function JGDLVideoManager(canvas) {
  this.pr_Main = null;
  this.canvas = canvas;
  this.ctx = canvas ? canvas.getContext('2d') : null;
  this.VideoSize = new JGDLVector(canvas ? canvas.width : 448, canvas ? canvas.height : 336);
  this.Images = {};
  this.LoadingImage = null;
}

JGDLVideoManager.prototype.LoadImage = function (path) {
  if (this.Images[path]) return this.Images[path];
  return null;
};

JGDLVideoManager.prototype.RegisterImage = function (path, img, frameSize) {
  var rec = { image: img, path: path, FrameSize: frameSize ? { fx: frameSize.fx || frameSize[0], fy: frameSize.fy || frameSize[1] } : null, Frames: [] };
  if (rec.FrameSize && img) {
    var fw = rec.FrameSize.fx;
    var fy = rec.FrameSize.fy;
    var cols = Math.floor(img.width / fw);
    var rows = Math.floor(img.height / fy);
    for (var j = 0; j < rows; j++)
      for (var i = 0; i < cols; i++)
        rec.Frames.push({ iLeft: i * fw, iTop: j * fy, iRight: (i + 1) * fw, iBottom: (j + 1) * fy });
  }
  this.Images[path] = rec;
  return rec;
};

JGDLVideoManager.prototype.Clear = function (color) {
  if (!this.ctx) return;
  this.ctx.fillStyle = color || '#000';
  this.ctx.fillRect(0, 0, this.VideoSize.fx, this.VideoSize.fy);
};

JGDLVideoManager.prototype.DrawImage = function (position, imageRecord, frameIndex, mirror) {
  if (!this.ctx || !imageRecord || !imageRecord.image) return;
  var fr = imageRecord.Frames[frameIndex != null ? frameIndex : 0];
  if (!fr) fr = imageRecord.Frames[0];
  if (!fr) {
    this.ctx.drawImage(imageRecord.image, position.fx, position.fy);
    return;
  }
  var w = fr.iRight - fr.iLeft;
  var h = fr.iBottom - fr.iTop;
  this.ctx.drawImage(imageRecord.image, fr.iLeft, fr.iTop, w, h, position.fx, position.fy, w, h);
};

JGDLVideoManager.prototype.DrawLoading = function (percent, gameName) {
  if (!this.ctx) return;
  var w = this.VideoSize.fx;
  var h = this.VideoSize.fy;
  this.ctx.fillStyle = '#fff';
  this.ctx.fillRect(0, 0, w, h);
  this.ctx.fillStyle = '#29166f';
  this.ctx.font = 'bold 11px Arial';
  this.ctx.textAlign = 'center';
  this.ctx.fillText('LOADING ' + (gameName || '') + ' ' + Math.floor(percent) + '% ...', w / 2, h - 26);
  this.ctx.strokeStyle = '#333';
  this.ctx.strokeRect(w / 2 - 85, h - 36, 248, 11);
  this.ctx.fillStyle = '#000080';
  this.ctx.fillRect(w / 2 - 82, h - 33, (percent / 100) * 242, 6);
};
