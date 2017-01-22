package controllers

import javax.inject._

import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, _}

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

  def points: Action[AnyContent] = Action { request =>
    val id = getSession(request).getSessionId
    val controller = manager.get(id)
    val game = controller.getGame
    Ok(JsObject(Seq(
      "player1" -> JsNumber(playerPoints(controller, 1)),
      "player2" -> JsNumber(playerPoints(controller, 2)),
      "cards" -> JsNumber(game.cardsInField.size + game.pack.size)
    )))
  }

  private def playerPoints(controller: SetController, no: Int): Int = {
    for ((key, value) <- controller.getPlayer) {
      if (value == no) {
        return controller.getGame.player.filter(p => p.name == key).map(p => p.points).sum
      }
    }
    0
  }

  def set(cardOne: String, cardTwo: String, cardThree: String): Action[AnyContent] = Action { request =>
    val id = getSession(request).getSessionId
    val controller = manager.get(id)
    val game = controller.getGame
    val cards = game.cardsInField
    val selected = cards.filter(c => c.name == cardOne) ++ cards.filter(c => c.name == cardTwo) ++ cards.filter(c => c.name == cardThree)
    game.player.find(p => p.name == id) match {
      case Some(value) => controller.getController.checkSet(selected, value)
    }
    Ok(JsBoolean(true))
  }

  def reset: Action[AnyContent] = Action { request =>
    val id = getSession(request).getSessionId
    val setController = manager.get(id)
    val controller = setController.getController
    controller.createNewGame()
    setController.getPlayer.keys.foreach(p => controller.addPlayer(p))
    controller.startGame()
    Ok(JsBoolean(true))
  }
}
