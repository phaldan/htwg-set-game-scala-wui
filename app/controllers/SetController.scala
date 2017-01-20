package controllers

import javax.inject._

import play.api.mvc._
import services.Counter

/**
  * This controller demonstrates how to use dependency injection to
  * bind a component into a controller class. The class creates an
  * `Action` that shows an incrementing count to users. The [[Counter]]
  * object is injected by the Guice dependency injection system.
  */
@Singleton
class SetController @Inject()(counter: Counter) extends Controller {

  /**
    * Create an action that responds with the [[Counter]]'s current
    * count. The result is plain text. This `Action` is mapped to
    * `GET /count` requests by an entry in the `routes` config file.
    */
  def count = Action {
    Ok(counter.nextCount().toString)
  }

  def exitApplication = Action {
    Ok("Exit Application")
  }

  def createNewGame = Action {
    Ok("Create new Game")
  }


  def addPlayer(name: String) = Action{
    Ok("Add new User name : " + name)
  }

  def cancelAddPlayer() = Action {
    Ok("Cancel add player")
  }

  def randomCardsInField() = Action{
    Ok("Random cards")
  }

  def finishGame() = Action{
    Ok("Finish Game")
  }

  def checkSet(cards: List[String]) = Action{
    Ok("Cards : " + cards)
  }

  def startGame() = Action{
    Ok("Start Game")
  }
}
