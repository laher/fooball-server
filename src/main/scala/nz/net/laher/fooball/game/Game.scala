package nz.net.laher.fooball.game

import scala.collection.mutable.ListBuffer
import scala.math
import scala.util.Random
import nz.net.laher.fooball.physics.Vector2D
import nz.net.laher.fooball.message.MessageComponent
import nz.net.laher.fooball.message.UserState

case class MobileState(var position: Vector2D = new Vector2D(), var velocity : Vector2D = new Vector2D(), var acceleration : Vector2D = new Vector2D()) {
  def update(dt : Int) {
    //p = p + dt * v + dt * dt * a / 2
    position = position.add(velocity.mul(dt)).add(acceleration.mul(dt * dt))
    //v = v + dt * a
    velocity = velocity.add(acceleration.mul(dt))
  }
  def update(state : UserState, dt : Int) {
    
  }
}

object Behaviour extends Enumeration {
	type Behaviour = Value
	val USER_CONTROLLED, AI_TOBASE, AI_ATTACK, AI_DEFEND = Value
	//var x = IDLE
}

case class Player(number : Int = 0, state : MobileState = MobileState(), behaviour : Behaviour.Value = Behaviour.AI_TOBASE)

case class Ball(state : MobileState = MobileState())
case class Team(name : String, players : List[Player])

case class User(teamname : String, selected: Int, state: UserState = UserState())
case class Score(s1 : Int = 0, s2 : Int = 0)

case class Game(id: String, seed: Int, start : Long, elapsed : Long = 0, teams : Tuple2[Team,Team], users : List[User], ball : Ball, score : Score = Score()) extends MessageComponent {
  def team(name : String) : Option[Team] = {
    if (teams._1.name.equals(name)) {
      Some(teams._1)
    } else if (teams._2.name.equals(name)) {
      Some(teams._2)
    } else {
      None
    }
  }
  def teamsList() : List[Team] = {
    List(teams._1, teams._2)
  }
}
case class GameView(id : Option[String], seed : Option[Int] = None,  start : Option[Long], elapsed : Option[Long], teams : Option[Tuple2[Team,Team]] = None, users : Option[List[User]] = None, ball : Option[Ball] = None, score : Option[Ball] = None)

object Game {
  def newPlayers11() : List[Player] = {
    List(Player(1),Player(2),Player(3),Player(4),Player(5),Player(6),Player(7),Player(8),Player(9),Player(10),Player(11))
  }
  def newGame(gameName : String, team1Name : String, team2Name : String) : Game = {
    val team1 = Team(team1Name, newPlayers11())
    val team2 = Team(team2Name, newPlayers11())
    val seed = new Random().nextInt //random seed :)
    Game(gameName, seed, new java.util.Date().getTime, 0, Tuple2(team1, team2), List(User(team1Name, 1)), Ball())
  }
  def newGame(gameName : String) : Game = {
    newGame(gameName, "1","2")
  }
  def initView(game: Game) : GameView = {
    GameView(Some(game.id), Some(game.seed), Some(game.start), Some(game.elapsed), Some(game.teams))
  }
  def view(game: Game) : GameView = {
    //todo: slice up game based on relevant changes
    GameView(Some(game.id), Some(game.seed), Some(game.start), Some(game.elapsed))
  }
}