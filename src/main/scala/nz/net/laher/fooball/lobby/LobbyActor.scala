package nz.net.laher.fooball.lobby

import akka.actor.Actor
import akka.event.Logging
import scala.collection.mutable.ListBuffer
import nz.net.laher.fooball.message.MessageComponent
import nz.net.laher.fooball.message.Start
import scala.actors.Futures._
import nz.net.laher.fooball.message.Stop
import nz.net.laher.fooball.game.Game
import nz.net.laher.fooball.serialization.Serializers
import org.json4s.native.Serialization
import org.mashupbots.socko.handlers.WebSocketBroadcastText
import nz.net.laher.fooball.message.NewGame
import nz.net.laher.fooball.game.GameWSActor
import nz.net.laher.fooball.game.GameActor
import akka.actor.Props
import org.mashupbots.socko.handlers.WebSocketBroadcaster
import nz.net.laher.fooball.game.GameWSActor
import akka.actor.ActorRef
import nz.net.laher.fooball.message.ListGames
import nz.net.laher.fooball.serialization.SerializationActor

object LobbyActor {
  val actorRef= "LobbyActor"
}
class LobbyActor(broadcaster : ActorRef, games : collection.mutable.Map[String,Game]) extends Actor {
  val log = Logging(context.system, LobbyActor.this)
  var running = false
  
  def receive = {
  	case m: LobbyMessage =>
  	  log.info("received message {}", m)
  	  m.components.foreach({ receive(_) })
    case s : Start => {
      if (!running) {
        log.info("start!")
    	start
      } else {
        log.info("already running")
      }
    }
    case s : Stop => {
      if (running) {
        log.info("stop!")
    	  running= false
      } else {
        log.info("already stopped")
      }
    }
    case NewGame(id) => {
      val g= Game.newGame(id)
      games.get(id) match {
        case Some(game) => {
        //already
        	log.error("Game {} already exists", id)
        }
        case None => {
	      log.info("New game with id {}",id)
	      log.info("Creating broadcaster at {}", GameWSActor.broadcasterRefPart+id)
	      //start broadcaster
	      val gameBroadcaster = context.actorOf(Props[WebSocketBroadcaster], GameWSActor.broadcasterRefPart+id)
	      val serializationActor = context.actorOf(Props(new SerializationActor(gameBroadcaster)), GameWSActor.serializerRefPart+id)
	      //start actor
	      val gameActor = context.actorOf(Props(new GameActor(g, serializationActor)), GameActor.actorRefPart+id)
	      games += id -> g
	      gameActor ! new Start
        }
      }
    }
    case ListGames => {
    	broadcaster ! games.keys
	}
  	case x : AnyRef =>
  	  log.info("received unknown message of type: {}", x.getClass())
  }

  def start = {
    running=true
	future {
	  while (running) { 
	    broadcaster ! games.keys
	    Thread.sleep(1000)
	  }
	}
  }
  
 /**
   * Echo the details of the web socket frame that we just received; but in upper case.
  private def listGames() {
    //log.info("Sending game list")
    //val broadcaster = context.actorFor("/user/"+LobbyWSHandler.broadcasterRef)
    implicit val formats= Serializers.defaultFormats
    val t = Serialization.write(games.keys)
    broadcaster ! WebSocketBroadcastText(t)
  }
   */

}
case class LobbyMessage(components : List[MessageComponent])