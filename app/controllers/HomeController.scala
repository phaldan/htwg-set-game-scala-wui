package controllers

import javax.inject._
import play.api.mvc.{AnyContent, _}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  val manager = new GameManager

  def index: Action[AnyContent] = Action { request =>
    val session = new SessionHandler(request.session)
    manager.get(session.getSessionId)
    Ok(views.html.index("Set - The Game")).withSession(session.getSession)
  }

  def ws: WebSocket = new MyWebSocket(manager).get
}
