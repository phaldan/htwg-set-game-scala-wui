package controllers

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import de.htwg.se.setGame.controller.Controller

import scala.collection.mutable

/**
  * @author Philipp Daniels
  */
class GameManager {
  private val system = ActorSystem("SetGame", ConfigFactory.parseString("akka {}"))
  private val games = mutable.Map[String, Controller]()
  private val freeGames = mutable.Stack[Controller]()

  def get(session: String): Controller = {
    games.get(session) match {
      case None =>
        newController(session)
      case Some(value) =>
        value
    }
  }

  private def newController(session: String): Controller = {
    val controller = if (freeGames.isEmpty) createPlayer1(session) else createPlayer2(session)
    println(session + " - new controller -" + controller.hashCode())
    games.put(session, controller)
    println("Manager: " + games.size + " sessions, " + freeGames.size + " open games")
    controller
  }

  private def createPlayer1(session: String) = {
    val controller = Controller(system)
    freeGames.push(controller)
    System.out.println(session + " - create as player 1")
    controller
  }

  private def createPlayer2(session: String) = {
    val controller = freeGames.pop()
    System.out.println(session + " - create as player 2")
    controller
  }
}
