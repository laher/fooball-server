package nz.net.laher.fooball.message

import scala.collection.mutable.ListBuffer


trait MessageComponent

case class UserState(keysDown : ListBuffer[Int] = new ListBuffer[Int]()) extends MessageComponent
case class UserInput(typ : String, value : Int) extends MessageComponent

case class Start() extends MessageComponent
case class Stop() extends MessageComponent
case class Status(full : Boolean) extends MessageComponent

case class NewGame(id: String) extends MessageComponent
case class JoinGame(id: String) extends MessageComponent
case object ListGames extends MessageComponent
