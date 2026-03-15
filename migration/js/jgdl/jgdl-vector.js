function JGDLVector(x, y) {
  this.fx = x != null ? x : 0;
  this.fy = y != null ? y : 0;
}
JGDLVector.prototype.atrib = function (x, y) {
  if (typeof x === 'object') {
    this.fx = x.fx; this.fy = x.fy;
  } else {
    this.fx = x; this.fy = y;
  }
};
JGDLVector.prototype.Floor = function () {
  this.fx = Math.floor(this.fx);
  this.fy = Math.floor(this.fy);
};
JGDLVector.prototype.Magnitude = function () {
  return Math.sqrt(this.fx * this.fx + this.fy * this.fy);
};
