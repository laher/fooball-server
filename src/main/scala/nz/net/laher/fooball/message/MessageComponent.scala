package nz.net.laher.fooball.message

import scala.collection.mutable.ListBuffer


trait MessageComponent

case class UserState(keysDown : ListBuffer[Int] = new ListBuffer[Int]()) extends MessageComponent
case class UserInput(typ : String, value : Int) extends MessageComponent
