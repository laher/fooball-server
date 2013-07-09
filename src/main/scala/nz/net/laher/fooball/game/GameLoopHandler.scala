package nz.net.laher.fooball.game

import akka.actor.Actor
import akka.event.Logging

class GameLoopHandler(game : Game) extends Actor {
  val log = Logging(context.system, GameLoopHandler.this)
  
  var running=false

  /**
   * Process incoming events
   */
  def receive = {
  	case m: Message =>
  	  log.info("received message {}", m)
  	case _ =>
  	  log.info("received unknown message of type: {}", (AnyRef)_)
  }
  def updateEntities(dt : Int) {
    //update ball
    game.ball.state.update(dt)
    //update players
    game.team1.players.foreach({ _.state.update(dt)})
    //update t2 players
    game.team2.players.foreach({ _.state.update(dt)})
    //check collisions
  }
  def start {
	  def loop : Boolean = running match {
	    case true => {
	      //update entities
	      updateEntities(1)
	      return loop
	    }
	    case _ => {
	      //do nothing ...
	      return false
	    }
	  }
  }

}