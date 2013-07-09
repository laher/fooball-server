package nz.net.laher.fooball.game

import scala.collection.mutable.ListBuffer
import scala.math
import nz.net.laher.fooball.game.physics.Vector2D

case class MobileState(var position: Vector2D = new Vector2D(), var velocity : Vector2D = new Vector2D(), var acceleration : Vector2D = new Vector2D()) {
  def update(dt : Int) {
    //p = p + dt * v + dt * dt * a / 2
    position = position.add(velocity.mul(dt)).add(acceleration.mul(dt * dt))
    //v = v + dt * a
    velocity = velocity.add(acceleration.mul(dt))
  }
}
case class Player(number : Int, state : MobileState = MobileState())
case class Ball(state : MobileState = MobileState())
case class Team(name : String, players : List[Player])

case class User(teamname : String, state: UserState = UserState())
case class Score(s1 : Int = 0, s2 : Int = 0)
case class Game(team1 : Team, team2 : Team, users : List[User], ball : Ball, score : Score = Score())
case class GameView(team1 : Option[Team] = None, team2 : Option[Team] = None, users : Option[List[User]] = None, ball : Option[Ball] = None, score : Option[Ball] = None)

object Game {
  def newPlayers11() : List[Player] = {
    List(Player(1),Player(2),Player(3),Player(4),Player(5),Player(6),Player(7),Player(8),Player(9),Player(10),Player(11))
  }
  def newGame(team1Name : String, team2Name : String) : Game = {
    val team1 = Team(team1Name, newPlayers11())
    val team2 = Team(team2Name, newPlayers11())
    Game(team1, team2, List(User(team1Name)), Ball())
  }
  def newGame() : Game = {
    newGame("1","2")
  }
  
  def view(game: Game) : GameView = {
    //todo: slice up game based on relevant changes
    GameView(Some(Team(game.team1.name, List(Player(1)))))
  }
}


trait MessageComponent
case class UserState(keysDown : ListBuffer[Int] = new ListBuffer[Int]()) extends MessageComponent
case class UserInput(typ : String, value : Int) extends MessageComponent

case class Message(components : List[MessageComponent])
case class MessageOld(state: Option[UserState] = None, input: Option[UserInput] = None)