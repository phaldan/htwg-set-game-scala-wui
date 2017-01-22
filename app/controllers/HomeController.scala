package controllers

import javax.inject._

import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString}
import play.api.mvc.{AnyContent, _}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

  val manager = new GameManager

  def index: Action[AnyContent] = Action { request =>
    val session = getSession(request)
    manager.get(session.getSessionId)
    Ok(views.html.index("Set - The Game")).withSession(session.getSession)
  }

  private def getSession(request: Request[AnyContent]) = new SessionHandler(request.session)

  def ws: WebSocket = new MyWebSocket(manager).get

  def cards: Action[AnyContent] = Action { request =>
    val id = getSession(request).getSessionId
    val controller = manager.get(id)
    Ok(JsObject(Seq(
      "player" -> JsNumber(controller.getPlayer(id)),
      "cards" -> JsArray(controller.getGame.cardsInField.map(c => {
        JsString(c.name)
      }))
    )))
  }
}
