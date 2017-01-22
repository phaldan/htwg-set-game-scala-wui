package controllers

import javax.inject._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee._
import play.api.mvc.{AnyContent, _}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  val manager = new GameManager

  def index: Action[AnyContent] = Action { request =>
    val session = new SessionHandler(request)
    manager.get(session.getSessionId)
    Ok(views.html.index("Set - The Game")).withSession(session.getSession)
  }

  def ws: WebSocket = WebSocket.using[String] { request =>
    // Log events to the console
    val in = Iteratee.foreach[String](println).map { _ =>
      println("Disconnected")
    }

    // Send a single 'Hello!' message
    val out = Enumerator("Hello!")

    (in, out)
  }
}
