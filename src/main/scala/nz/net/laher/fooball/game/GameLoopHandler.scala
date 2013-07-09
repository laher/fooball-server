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
  def updateEntities() {
    //update ball
    //update players
    //update t2 players
    //
  }
  def start {
	  def loop : Boolean = running match {
	    case true => {
	      //update entities
	      updateEntities()
	      return loop
	    }
	    case _ => {
	      //do nothing ...
	      return false
	    }
	  }
  }

}