package controllers

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import play.api.Logger

import scala.collection.mutable

/**
  * @author Philipp Daniels
  */
class GameManager {
  private val system = ActorSystem("SetGame", ConfigFactory.parseString("akka {}"))
  private val games = mutable.Map[String, SetController]()
  private val freeGames = mutable.Stack[SetController]()

  def get(session: String): SetController = {
    games.get(session) match {
      case None =>
        newController(session)
      case Some(value) =>
        value
    }
  }

  private def newController(session: String) = {
    val controller = if (freeGames.isEmpty) createPlayer1(session) else createPlayer2(session)
    Logger.debug(GameManager.NewController.format(session, controller.hashCode()))
    games.put(session, controller)
    Logger.debug(GameManager.Stats.format(games.size, freeGames.size))
    controller
  }

  private def createPlayer1(session: String) = {
    val controller = new SetController(system)
    controller.getController.createNewGame()
    controller.getController.addPlayer(session)
    controller.getController.startGame()
    controller.getPlayer(session) = 1
    freeGames.push(controller)
    Logger.debug(GameManager.AddPlayer1.format(session))
    controller
  }

  private def createPlayer2(session: String) = {
    val controller = freeGames.pop()
    controller.getController.addPlayer(session)
    controller.getPlayer(session) = 2
    Logger.debug(GameManager.AddPlayer2.format(session))
    controller
  }
}

object GameManager {
  val AddPlayer1 = "%s - create as player 1"
  val AddPlayer2 = "%s - create as player 2"
  val NewController = "%s - new controller (%d)"
  val Stats = "Manager: %d running games, %d open games"
  def apply: GameManager = new GameManager()
}
