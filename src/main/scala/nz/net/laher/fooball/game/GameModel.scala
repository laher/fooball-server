package nz.net.laher.fooball.game

import scala.collection.mutable.ListBuffer
import scala.math
object Physics2d {
  def angleFrom0(xy : Tuple2[Int,Int]) : Int = {
    0
  }
  def distanceFrom0(xy : Tuple2[Int,Int]) : Int = {
    0
  }
}
class Vector(var angle : Int = 0, var magnitude : Int = 0) {
   def this(xy : Tuple2[Int,Int]) {
     this(Physics2d.angleFrom0(xy), Physics2d.distanceFrom0(xy))
   }
   def getXy : Tuple2[Int,Int] = {
         Tuple2[Int,Int](
        		(math.cos(angle * math.Pi / 180) * magnitude).toInt,
        		(math.sin(angle * math.Pi / 180) * magnitude).toInt
        )
   }
    def setXy(xy : Tuple2[Int,Int]) = {
        angle = Physics2d.angleFrom0(xy)
        magnitude = Physics2d.distanceFrom0(xy)
    }
    def add(v : Vector) : Vector = {
      val xy= this.getXy
      val vxy= v.getXy
      var xy3= Tuple2[Int,Int](xy._1+vxy._1, xy._2+vxy._2)
      return new Vector(xy3)
    }
    
   /*
                        
                        sub : function(v) {
                                var s1 = this.getXy();
                                var s2 = v.getXy();
                                var s3 = [s1[0]-s2[0], s1[1]-s2[1]];
                                return FOOBALL.physics2d.newVector(FOOBALL.physics2d.angleFrom0(s3),
                                                FOOBALL.physics2d.distanceFrom0(s3));
                        },
                        mul : function(dt) {
                                return FOOBALL.physics2d.newVector(this.angle,this.magnitude * dt);
                        },
                        //TODO mul, rmul
                        mulSingle : function(v) {
                                return this.magnitude*v.magnitude*Math.cos(FOOBALL.physics2d.normalizeAngle(this.angle-v.angle)*Math.PI/180.0);
                        },
                        copy : function() {
                                return FOOBALL.physics2d.newVector(this.angle,
                                                        this.magnitude);
                        },
                        normalize : function() {
                                if (this.magnitude !== 0) {
                                        this.magnitude = 1;
                                }
                        }
*/
}
case class MobileState(posVector: Vector = new Vector(), speedVector : Vector = new Vector())
case class Player(number : Int, state : MobileState = MobileState())
case class Ball(state : MobileState = MobileState())
case class Team(name : String, players : List[Player])

case class User(teamname : String, state: UserState = UserState())
case class Score(s1 : Int = 0, s2 : Int = 0)
case class Game(team1 : Team, team2 : Team, users : List[User], ball : Ball, score : Score = Score())
case class GameView(team1 : Option[Team] = None, team2 : Option[Team] = None, users : Option[List[User]] = None, ball : Option[Ball] = None, score : Option[Ball] = None)

trait MessageComponent
case class UserState(keysDown : ListBuffer[Int] = new ListBuffer[Int]()) extends MessageComponent
case class UserInput(typ : String, value : Int) extends MessageComponent

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
    GameView(Some(Team(game.team1.name, List(Player(1)))))
  }
}
case class Message(components : List[MessageComponent])
case class MessageOld(state: Option[UserState] = None, input: Option[UserInput] = None)