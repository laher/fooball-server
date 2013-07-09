package nz.net.laher.fooball.game.physics

object Physics2D {

  def angleFrom0(xy: Tuple2[Double, Double]): Double = {
    angle(new Tuple2(0, 0), xy)
  }
  def angle(xy1: Tuple2[Double, Double], xy2: Tuple2[Double, Double]): Double = {
    normalizeAngle(
      ((math.atan2(xy2._2 - xy1._2, xy2._1 - xy1._1) * 180) / math.Pi).toDouble)
  }
  def distanceFrom0(xy: Tuple2[Double, Double]): Double = {
    distance(new Tuple2(0, 0), xy)
  }
  def distance(xy1: Tuple2[Double, Double], xy2: Tuple2[Double, Double]): Double = {
    math.sqrt(math.pow(xy2._1 - xy1._1, 2) + math.pow(xy2._2 - xy1._2, 2)).toDouble
  }
  def normalizeAngle(a: Double): Double = {
    return (a + 360) % 360;
  }
  def angleIsForward(a: Double, isUp: Boolean): Boolean = {
    var b = normalizeAngle(a);
    if (b <= 90 || b > 270) {
      isUp
    } else {
      !isUp
    }
  }

}