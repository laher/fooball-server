import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.TestKit
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import nz.net.laher.fooball.lobby.LobbyActor
import nz.net.laher.fooball.message.Start
import nz.net.laher.fooball.message.NewGame
import nz.net.laher.fooball.serialization.Serializers
import org.json4s.native.Serialization
import org.mashupbots.socko.handlers.WebSocketBroadcastText
import nz.net.laher.fooball.message.ListGames
import akka.testkit.TestProbe
 
object LobbyLoopSpec {
  class EchoActor extends Actor {
    def receive = {
      case x ⇒ println(x)
    }
  }
}
 
@RunWith(classOf[JUnitRunner])
class LobbyLoopSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpec with MustMatchers with BeforeAndAfterAll {
 
  def this() = this(ActorSystem("LobbyLoopSpec"))
 
  import MySpec._
 
  override def afterAll {
    system.shutdown()
  }
 
  "A LobbyLoop actor" must {
 
    "List games appropriately" in {
      val probe1 = TestProbe()
      val loophandler = system.actorOf(Props(new LobbyActor(probe1.ref)))
      //echo ! "hello world"
      //loophandler ! new Start()
      loophandler ! ListGames
      probe1.expectMsg(WebSocketBroadcastText("[]"))
      loophandler ! new NewGame("1")
      loophandler ! ListGames
      probe1.expectMsg(WebSocketBroadcastText("[\"1\"]"))
    }
 
  }
}