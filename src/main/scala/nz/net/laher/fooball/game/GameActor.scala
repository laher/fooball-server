package nz.net.laher.fooball.game

import akka.actor.Actor
import scala.actors.Futures._
import akka.event.Logging
import nz.net.laher.fooball.serialization.Serializers
import org.mashupbots.socko.handlers.WebSocketBroadcastText
import org.json4s.native.Serialization
import nz.net.laher.fooball.message.MessageComponent
import nz.net.laher.fooball.message.UserInput
import nz.net.laher.fooball.message.UserState
import akka.actor.ActorRef
import nz.net.laher.fooball.message.Start
import nz.net.laher.fooball.message.Stop

case class GameMessage(components : List[MessageComponent])

object GameActor {
  val actorRefPart= "GameActor_"
}
class GameActor(game : Game, broadcaster : ActorRef) extends Actor {
  val log = Logging(context.system, GameActor.this)
  
  var running=false

  /**
   * Process incoming events
   */
  def receive = {
  	case m: GameMessage =>
  	  log.info("received message {}", m)
  	  m.components.foreach({ receive(_)})
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
    case event : UserInput => {
    	  //update 
	  var user= game.users(0)
	  if ("kd".equals(event.typ)) {
	    if(!user.state.keysDown.contains(event.value)) {
			user.state.keysDown += event.value
	    }
	  } else if ("ku".equals(event.typ)) {
		  user.state.keysDown.dropWhile({ event.value == _})
	  }
   }
   case state : UserState => {
    //update 
	var user= game.users(0)
	user.state.keysDown.clear()
	state.keysDown.foreach({ user.state.keysDown += _ })
	Game.view(game)

   }
  	case _ =>
  	  log.info("received unknown message of type: {}", (AnyRef)_)
  }
  
  
	
  
  def updateEntities(dt : Int) {
    //update ball
    game.ball.state.update(dt)
    //update players
    game.teamsList.foreach({ 
      t => t.players.foreach ( { _ match {
        case Player(_, _, Behaviour.AI_ATTACK) => println("attack") 
        case Player(_, _, Behaviour.AI_DEFEND) => println("defend") 
        case Player(_, _, Behaviour.AI_TOBASE) => println("tobase") 
        case Player(_, _, Behaviour.USER_CONTROLLED) => println("user") 
      }})
        
    })
    
    //update user-selected
	game.users.foreach({ u => game.team(u.teamname).get.players(u.selected).state.update(u.state, dt) })
    //update user non-selected
	//game.teamsList().foreach({ t => t.players.foreach({ p => p.react() }) })
    //update players
    game.teams._1.players.foreach({ _.state.update(dt)})
    //update t2 players
    game.teams._2.players.foreach({ _.state.update(dt)})
    //check collisions
  }
  def start {
    running=true
    //just once - send the whole game ...
    broadcaster ! game
	future {
	  while (running) { 
	    //  pump events
		//  update input
		//  update AI
		//  update physics
	    //render
	    //regularly just send a view of the game
	    broadcaster ! Game.view(game)
	    Thread.sleep(1000)
	  }
	}
    /*
	  def loop : Boolean = running match {
	    case true => {
		//  pump events
		//  update input
		//  update AI
		//  update physics
	      updateEntities(1)
	      //broadcast state back
	      
		//  render
	      return loop
	    }
	    case _ => {
	      //do nothing ...
	      return false
	    }
	  }
	  * 
	  */
  }
  
 /*
   * Echo the details of the web socket frame that we just received; but in upper case.
   
  private def writeWebSocketResponseBroadcast(view: GameView) {
    broadcaster ! view
    *
    log.info("Sending response")
    //todo
    val broadcaster = context.actorFor("/user/"+GameWSActor.broadcasterRefPart+game.id)
    implicit val formats= Serializers.defaultFormats
    val t = Serialization.write(view)
    broadcaster ! WebSocketBroadcastText(t)
    *
  }*/
}
