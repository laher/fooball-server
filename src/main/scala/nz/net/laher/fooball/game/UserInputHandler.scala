package nz.net.laher.fooball.game

import akka.event.Logging
import akka.actor.Actor
import org.mashupbots.socko.events.WebSocketFrameEvent
import org.mashupbots.socko.events.HttpRequestEvent
import nz.net.laher.fooball.serialization.Serializers
import org.json4s.native.Serialization
import org.mashupbots.socko.handlers.WebSocketBroadcastText

class UserInputHandler(game : Game) extends Actor {
  val log = Logging(context.system, UserInputHandler.this)
  

/**
   * Process incoming events
   */
  def receive = {
  	case m: Message =>
    log.info("Received input message - Message")
  	  m.components.foreach({ receive(_)})
    case event: UserInput =>
    log.info("Received input message - UserInput")
      // process it ...
      val gv= updateModel(event)
      writeWebSocketResponseBroadcast(gv)
    case state: UserState =>
    log.info("Received input message - UserState")
      // process it ...
       val gv= updateModel(state)
      writeWebSocketResponseBroadcast(gv)
    case _ => {
      log.info("received unknown message of type: {}", (AnyRef)_)
    }
  }

	private def updateModel(event: UserInput): GameView = {
	  //update 
	  var user= game.users(0)
	  if ("kd".equals(event.typ)) {
	    if(!user.state.keysDown.contains(event.value)) {
			user.state.keysDown += event.value
	    }
	  } else if ("ku".equals(event.typ)) {
		  user.state.keysDown.dropWhile({ event.value == _})
	  }
	  Game.view(game)
	}
	
	private def updateModel(state: UserState) : GameView = {
	  //update 
		var user= game.users(0)
		user.state.keysDown.clear()
 		state.keysDown.foreach({ user.state.keysDown. += _ })
 		Game.view(game)
	}
 	
	 /**
   * Echo the details of the web socket frame that we just received; but in upper case.
   */
  private def writeWebSocketResponseBroadcast(view: GameView) {
    log.info("Sending response")
    val broadcaster = context.actorFor("/user/webSocketBroadcaster")
    implicit val formats= Serializers.defaultFormats
    val t = Serialization.write(view)
    broadcaster ! WebSocketBroadcastText(t)
  }
}