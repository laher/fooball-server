// This class originates from the socko examples. Keeping copyright and license accordingly
//
// Copyright 2012 Vibul Imtarnasan, David Bolton and Socko contributors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package nz.net.laher.fooball.websocket

import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import org.mashupbots.socko.events.HttpRequestEvent
import org.mashupbots.socko.events.WebSocketFrameEvent
import org.mashupbots.socko.handlers.WebSocketBroadcastText
import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.event.Logging
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read,write,formats}
import nz.net.laher.fooball.game.UserInputHandler
import nz.net.laher.fooball.game.Game
import nz.net.laher.fooball.game.GameMessage
import nz.net.laher.fooball.serialization.Serializers
import nz.net.laher.fooball.game.GameView
import nz.net.laher.fooball.game.GameLoopHandler

/**
 * Web Socket processor for fooball input
 */
class GameWSHandler(id : String) extends Actor {
  val log = Logging(context.system, GameWSHandler.this)
  
  /**
   * Process incoming events
   */
  def receive = {
    case event: WebSocketFrameEvent =>
      handleInput(event)
      // Echo web socket text frames
   	  writeWebSocketResponseBroadcast(event)
      context.stop(self)
    case _ => {
      log.info("received unknown message of type: {}",(AnyRef)_)
      context.stop(self)
    }
  }

  private def handleInput(event: WebSocketFrameEvent) {
    log.info("TextWebSocketFrame: ." + event.readText + ".")
    //implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[UserInput], classOf[UserState])))
    implicit val formats = Serializers.longFormats
    var message = read[GameMessage](event.readText)
    log.info("Received "+message)
    
    val inputHandler = context.actorFor("/user/"+GameLoopHandler.actorRefPart+"/"+id)
    message.components.foreach({ inputHandler ! _ })
  }
  
  //hmm
  
  private def writeWebSocketResponseBroadcast(event: WebSocketFrameEvent) {
    log.info("TextWebSocketFrame: " + event.readText)
    val dateFormatter = new SimpleDateFormat("HH:mm:ss")
    val time = new GregorianCalendar()
    val ts = dateFormatter.format(time.getTime())
    val broadcaster = context.actorFor("/user/webSocketBroadcaster")
    /*
    //implicit val formats= Serializers.defaultFormats
    implicit val formats= Serializers.defaultFormats
    val t = write(game)
    */
    broadcaster ! WebSocketBroadcastText(ts + " received OK")
  }
  private def writeWebSocketResponseDirect(event: WebSocketFrameEvent) {
	    log.info("TextWebSocketFrame: " + event.readText)

	    val dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	    val time = new GregorianCalendar()
	    val ts = dateFormatter.format(time.getTime())
	    val inputHandler = context.actorFor("/user/userInputHandler")
	    event.writeText(ts + " " + event.readText.toUpperCase())
	  }
}

