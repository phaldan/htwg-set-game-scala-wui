package controllers

import de.htwg.se.setGame.controller.{NewGame, StartGame, UpdateGame}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.mvc.WebSocket
import scala.swing.Reactor

/**
  * @author Philipp Daniels
  */
class MyWebSocket(private val manager: GameManager) extends Reactor {
  def get: WebSocket = WebSocket.using[String] { request =>
    val session = new SessionHandler(request.session)
    val controller = manager.get(session.getSessionId)
    val in = Iteratee.foreach[String](_ => {}).map { _ =>
      Logger.debug(MyWebSocket.Disconnect.format(session.getSessionId))
    }
    val (out, channel) = Concurrent.broadcast[String]
    Logger.debug(MyWebSocket.Connected.format(session.getSessionId))
    listenTo(controller)

    reactions += {
      case e: NewGame =>
        Logger.debug(MyWebSocket.EventNewGame.format(session.getSessionId))
        channel.push(e.getClass.getName)
      case e: StartGame =>
        Logger.debug(MyWebSocket.EventStartGame.format(session.getSessionId))
        channel.push(e.getClass.getName)
      case e: UpdateGame =>
        Logger.debug(MyWebSocket.EventUpdateGame.format(session.getSessionId))
        channel.push(e.getClass.getName)
    }

    (in, out)
  }
}

object MyWebSocket {
  val Disconnect = "%s - disconnect from websocket"
  val Connected = "%s - connected to websocket"
  val EventNewGame = "%s - websocket received 'NewGame' event"
  val EventStartGame = "%s - websocket received 'StartGame' event"
  val EventUpdateGame = "%s - websocket received 'UpdateGame' event"
}
