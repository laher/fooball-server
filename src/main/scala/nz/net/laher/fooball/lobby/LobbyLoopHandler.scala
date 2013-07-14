package nz.net.laher.fooball.lobby

import akka.actor.Actor
import akka.event.Logging
import scala.collection.mutable.ListBuffer
import nz.net.laher.fooball.message.MessageComponent
object LobbyLoopHandler {
  val actorRef= ""
}
class LobbyLoopHandler extends Actor {
  val log = Logging(context.system, LobbyLoopHandler.this)
  
  def receive = {
  	case m: LobbyMessage =>
  	  log.info("received message {}", m)
  	  m.components.foreach({ processInput(_)})
  	case _ =>
  	  log.info("received unknown message of type: {}", (AnyRef)_)
  }
  def processInput(component : MessageComponent) {
     
  }
}
case class LobbyMessage(components : List[MessageComponent])