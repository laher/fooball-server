package nz.net.laher.fooball.serialization

import akka.actor.Actor
import akka.event.Logging
import org.json4s.native.Serialization
import nz.net.laher.fooball.message.MessageComponent
import akka.actor.ActorRef
import org.mashupbots.socko.handlers.WebSocketBroadcastText

//serializes to json & wraps into a WebSocketBroadcastText
class SerializationActor(onward : ActorRef) extends Actor {
  val log = Logging(context.system, SerializationActor.this)
  
  def receive = {
    case m : AnyRef => {
      //log.debug("Serializing: {}", m)
      implicit val formats= Serializers.defaultFormats
      val t = Serialization.write(m)
      //log.debug("forwarding: {}", t)
      onward ! WebSocketBroadcastText(t)
    }
  }
    
}