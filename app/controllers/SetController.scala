package controllers

import akka.actor.ActorSystem
import de.htwg.se.setGame.controller._
import de.htwg.se.setGame.model.Game

import scala.collection.mutable
import scala.swing.Reactor

class SetController(system: ActorSystem) extends Reactor {
  private var game = None : Option[Game]
  private val controller = Controller(system)
  listenTo(controller)
  private val player = mutable.Map[String, Int]()

  reactions += {
    case e:AddPlayer => game = Some(e.game)
    case e:PlayerAdded => game = Some(e.game)
    case e:NewGame => game = Some(e.game)
    case e:StartGame => game = Some(e.game)
    case e:UpdateGame => game = Some(e.game)
  }

  def getController: Controller = controller
  def getPlayer: mutable.Map[String, Int] = player
  def getGame: Game = game.get
}
