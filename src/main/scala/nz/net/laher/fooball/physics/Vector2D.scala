package nz.net.laher.fooball.physics


class Vector2D(angle: Double = 0, magnitude: Double = 0) {
  def this(xy: Tuple2[Double, Double]) {
    this(Physics2D.angleFrom0(xy), Physics2D.distanceFrom0(xy))
  }
  def getXy(): Tuple2[Double, Double] = {
    Tuple2[Double, Double](
      (math.cos(angle * math.Pi / 180) * magnitude).toDouble,
      (math.sin(angle * math.Pi / 180) * magnitude).toDouble)
  }
  /*
  def setXy(xy: Tuple2[Double, Double]) = {
    angle = Physics2D.angleFrom0(xy)
    magnitude = Physics2D.distanceFrom0(xy)
  }
  * 
  */
  def add(v: Vector2D): Vector2D = {
    val xy = this.getXy
    val vxy = v.getXy
    var xy3 = Tuple2[Double, Double](xy._1 + vxy._1, xy._2 + vxy._2)
    return new Vector2D(xy3)
  }
  def sub(v: Vector2D): Vector2D = {
    var xy1 = this.getXy()
    var xy2 = v.getXy()
    var xy3 = new Tuple2(xy2._1 - xy1._1, xy2._2 - xy1._2);
    new Vector2D(xy3)
  }
  def mul(dt: Double): Vector2D = {
    new Vector2D(angle, magnitude * dt)
  }
  def copy(): Vector2D = {
    new Vector2D(angle, magnitude)
  }
  
  def normalize() : Vector2D = {
    if (magnitude != 0) {
      new Vector2D( angle, 1)
    }
    copy()
  }

  /*
    //TODO mul, rmul
    mulSingle : function(v) {
            return this.magnitude*v.magnitude*Math.cos(FOOBALL.physics2d.normalizeAngle(this.angle-v.angle)*Math.PI/180.0);
    }
*/
}