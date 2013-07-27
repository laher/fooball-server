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

import org.mashupbots.socko.events.HttpResponseStatus
import org.mashupbots.socko.events.WebSocketHandshakeEvent
import org.mashupbots.socko.handlers.WebSocketBroadcastText
import org.mashupbots.socko.handlers.WebSocketBroadcaster
import org.mashupbots.socko.handlers.WebSocketBroadcasterRegistration
import org.mashupbots.socko.infrastructure.Logger
import org.mashupbots.socko.routes._
import org.mashupbots.socko.webserver.WebServer
import org.mashupbots.socko.webserver.WebServerConfig
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import nz.net.laher.fooball.game.Game
import nz.net.laher.fooball.game.GameActor
import nz.net.laher.fooball.game.UserInputHandler
import org.mashupbots.socko.handlers.StaticContentHandler
import org.mashupbots.socko.handlers.StaticContentHandlerConfig
import java.io.File
import com.typesafe.config.ConfigFactory
import akka.routing.FromConfig
import org.mashupbots.socko.handlers.StaticFileRequest
import org.mashupbots.socko.events.HttpResponseMessage
import akka.actor.ActorRef
import nz.net.laher.fooball.lobby.LobbyActor
import nz.net.laher.fooball.message.Start
import nz.net.laher.fooball.game.GameWSActor
import nz.net.laher.fooball.lobby.LobbyWSActor
import nz.net.laher.fooball.serialization.SerializationActor

/**
 * Fooball app.
 * 
 * Static content handler for some test html
 * 1 WebSocketBroadcaster for the 'lobby'
 * Multiple WebSocketBroadcasters for games - 1 per game. 
 *
 * With `org.mashupbots.socko.processors.WebSocketBroadcaster`, you can broadcast messages to all registered web
 * socket connections
 *
 *  Navigate to `http://localhost:8888/`.
 */
object FooballServerApp extends Logger {
	val games = collection.mutable.Map[String,Game]()
    val actorConfig = """
      my-pinned-dispatcher {
        type=PinnedDispatcher
        executor=thread-pool-executor
      }
      akka {
        event-handlers = ["akka.event.slf4j.Slf4jEventHandler"]
        loglevel=DEBUG
        actor {
          deployment {
            /static-file-router {
              router = round-robin
              nr-of-instances = 5
            }
          }
        }
      }"""
  val actorSystem = ActorSystem("FooBallSystem", ConfigFactory.parseString(actorConfig))
  //val userInputHandler = actorSystem.actorOf(Props(new UserInputHandler(game)), "userInputHandler")
  
  //TODO overrideable at startup ..
  val contentDir = new File("src/main/web/").getAbsolutePath()
  
  val handlerConfig = StaticContentHandlerConfig(
      rootFilePaths = Seq(contentDir),
      tempDir = new File("/tmp/"),
      serverCacheTimeoutSeconds = 10)
      
  val staticContentHandlerRouter = actorSystem.actorOf(Props(new StaticContentHandler(handlerConfig))
      .withRouter(FromConfig()).withDispatcher("my-pinned-dispatcher"), "static-file-router")

  val lobbyWebSocketBroadcaster = actorSystem.actorOf(Props[WebSocketBroadcaster], LobbyWSActor.broadcasterRef)
  val lobbySerializer = actorSystem.actorOf(Props(new SerializationActor(lobbyWebSocketBroadcaster)), LobbyWSActor.serializerRef)
  val lobbyLoopHandler = actorSystem.actorOf(Props(new LobbyActor(lobbySerializer, games)), LobbyActor.actorRef)
      //
  // STEP #2 - Define Routes
  // Each route dispatches the request to a newly instanced `WebSocketHandler` actor for processing.
  // `WebSocketHandler` will `stop()` itself after processing the request. 
  //
  val routes = Routes({

    case HttpRequest(httpRequest) => httpRequest match {
      case GET(Path(fileName)) => {
        if (fileName.endsWith("/")) {
        	httpRequest.response.redirect(fileName + "index.html")
        } else {
        	staticContentHandlerRouter ! new StaticFileRequest(httpRequest, new File(contentDir, fileName))
        }
      }
      case Path("/favicon.ico") => {
        // If favicon.ico, just return a 404 because we don't have that file
        httpRequest.response.write(HttpResponseStatus.NOT_FOUND)
      }
      case _ => {
        httpRequest.response.write(HttpResponseStatus.NOT_FOUND)
      }
    }
    //TODO: per-user broadcaster
    case WebSocketHandshake(wsHandshake) => wsHandshake match {
      //list games
      case Path("/lobby") => {
        // To start Web Socket processing, we first have to authorize the handshake.
        // This is a security measure to make sure that web sockets can only be established at your specified end points.
        wsHandshake.authorize(onComplete = Some((event: WebSocketHandshakeEvent) => {
          log.info("username: {}", event.username)
          // Register this connection with the broadcaster
          // We do this AFTER handshake has been completed so that the server does not send data to client until
          // after the client gets a handshake response
          lobbyWebSocketBroadcaster ! new WebSocketBroadcasterRegistration(event)
        }))

      }
      //game
      case PathSegments("game" :: id :: Nil) => {
        val address= "/user/" + LobbyActor.actorRef + "/" + GameWSActor.broadcasterRefPart + id
        val userAddress= "/user/" + LobbyActor.actorRef + "/" + GameWSActor.userBroadcasterRefPart + id
        log.debug("Registering with broadcaster at {}", address)
        val gameBroadcaster= actorSystem.actorFor(address)
        val userBroadcaster= actorSystem.actorFor(userAddress)
        // To start Web Socket processing, we first have to authorize the handshake.
        // This is a security measure to make sure that web sockets can only be established at your specified end points.
        wsHandshake.authorize(onComplete = Some((event: WebSocketHandshakeEvent) => {
           gameBroadcaster ! new WebSocketBroadcasterRegistration(event)
           userBroadcaster ! new WebSocketBroadcasterRegistration(event)
        	}))
		        //TODO: add user to game
        //val aAddress= "/user/" + LobbyActor.actorRef + "/" + GameActor.actorRefPart + id
      	//aAddress ! 
      }
    }

    case WebSocketFrame(wsFrame) => wsFrame match {
      case Path("/lobby") => {
    	  // Once handshaking has taken place, we can now process frames sent from the client
    	  actorSystem.actorOf(Props(new LobbyWSActor())) ! wsFrame
      }
      case PathSegments("game" :: id :: Nil) => {
    	  // Once handshaking has taken place, we can now process frames sent from the client
    	  games.get(id) match {
    	    case Some(game) => {
    	    	actorSystem.actorOf(Props(new GameWSActor(id, game))) ! wsFrame
    	    }
    	    case None => {
    	      log.error("Game doesnt exist")
    	    }
    	  }
      }
      case _ => {
              log.error("Unhandled endpoint: {}", wsFrame.endPoint)

      }
      
    }

  })

  //
  // STEP #3 - Start and Stop Socko Web Server
  //
  def main(args: Array[String]) {
    val webServer = new WebServer(WebServerConfig(), routes, actorSystem)
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run { webServer.stop() }
    })
    webServer.start()
    val inputHandler = actorSystem.actorFor("/user/" + LobbyActor.actorRef)
    inputHandler ! Start()
    System.out.println("Navigate to http://localhost:8888/ to start playing")
  }
}
