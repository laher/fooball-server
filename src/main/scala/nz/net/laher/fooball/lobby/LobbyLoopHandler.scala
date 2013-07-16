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
import nz.net.laher.fooball.websocket.LobbyWSHandler
import nz.net.laher.fooball.message.NewGame
import nz.net.laher.fooball.websocket.GameWSHandler
import nz.net.laher.fooball.game.GameLoopHandler
import akka.actor.Props
import org.mashupbots.socko.handlers.WebSocketBroadcaster
import nz.net.laher.fooball.websocket.GameWSHandler

object LobbyLoopHandler {
  val actorRef= "LobbyLoopHandler"
}
class LobbyLoopHandler extends Actor {
  val log = Logging(context.system, LobbyLoopHandler.this)
  var running = false
  var games = ListBuffer[Game]()
  
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
      if(games.map({ _.id }).contains(id)) {
        //already
        log.error("Game {} already exists", id)
      } else {
      log.info("New game with id {}",id)
      //start actor
      context.actorOf(Props(new GameLoopHandler(g)), GameLoopHandler.actorRefPart+id)
      //start broadcaster
      log.info("Creating broadcaster at {}", GameWSHandler.broadcasterRefPart+id)
      context.actorOf(Props[WebSocketBroadcaster], GameWSHandler.broadcasterRefPart+id)
      games+= g
      }
    }
  	case _ =>
  	  log.info("received unknown message of type: {}", (AnyRef)_)
  }

  def start = {
    running=true
	future {
	  while (running) { 
	    //go
	    writeWebSocketResponseBroadcast
	    Thread.sleep(1000)
	  }
	}
  }
  
 /**
   * Echo the details of the web socket frame that we just received; but in upper case.
   */
  private def writeWebSocketResponseBroadcast() {
    //log.info("Sending game list")
    val broadcaster = context.actorFor("/user/"+LobbyWSHandler.broadcasterRef)
    implicit val formats= Serializers.defaultFormats
    val t = Serialization.write(games.map({ _.id }))
    broadcaster ! WebSocketBroadcastText(t)
  }
}
case class LobbyMessage(components : List[MessageComponent])