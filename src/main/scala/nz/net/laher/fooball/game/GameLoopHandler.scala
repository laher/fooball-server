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
	  def loop : Boolean = running match {
	    case true => {
		//  pump events
		//  update input
		//  update AI
		//  update physics
		//  render
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