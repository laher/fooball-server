import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.formats
import org.json4s.native.Serialization.read
import org.json4s.native.Serialization.write
import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import nz.net.laher.fooball.game.GameMessage
import nz.net.laher.fooball.serialization.Serializers
import scala.collection.mutable.MutableList
import scala.collection.mutable.ListBuffer
import nz.net.laher.fooball.game.Game
import nz.net.laher.fooball.physics.Vector2D
import nz.net.laher.fooball.message.UserInput
import nz.net.laher.fooball.message.UserState

@RunWith(classOf[JUnitRunner])
class ExampleSpec extends FunSpec {
	val sample= """{"typ":"kd","value":1}"""
	val s2="""{,"input":{"typ":"kd","value":1}}"""
	val s3="""{"components":[{"jsonClass":"UserInput","typ":"kd","value":1},{"jsonClass":"UserInput","typ":"kd","value":2}]}"""
	


  describe("json") {

    it("should serialise a UserInput and a Message") {
	    implicit var formats = Serialization.formats(NoTypeHints)
	    var x= write(UserInput("kd", 1))
	    assert(x === """{"typ":"kd","value":1}""")
	    
	    formats= Serialization.formats(ShortTypeHints(List(classOf[UserInput], classOf[UserState])))
	    var z= write(GameMessage(components = List(UserInput("kd", 1), UserInput("kd", 2))))
	    println(z)
	    assert(z === s3)
    }
    
    it("should use field serializers") {
      implicit var formats= Serializers.defaultFormats 
      var z= write(GameMessage(components = List(UserInput("kd", 1), UserInput("ku", 2), UserState(ListBuffer[Int](1,2,3)))))
	  println(z)
      var y= write(List(UserInput("kd", 1), UserInput("ku", 2), UserState(ListBuffer[Int](1,2,3))))
	  println(y)
    }
    it("should serialise a Game") {
      implicit var formats= Serializers.defaultFormats 
      var z= write(Game.newGame("1"))
      println(z)
    }
    
    it("should deserialise a Message, UserInput and Vector") {
	    implicit val formats = Serialization.formats(NoTypeHints)
	    
	    var input = read[UserInput](sample)
    	assert(input === UserInput("kd", 1))
    	
	    var vec = read[Vector2D]("""{"angle":1,"magnitude":1}""")
    	assert(vec === new Vector2D(1, 1))
    }
    
    //it("should throw NoSuchElementException if an empty stack is popped") (pending)
  }
}