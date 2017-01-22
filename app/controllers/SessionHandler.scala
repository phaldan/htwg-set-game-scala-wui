package controllers

import play.api.mvc.{AnyContent, Request, Session}

/**
  * @author Philipp Daniels
  */
class SessionHandler(private val request: Request[AnyContent]) {
  private val key = "session"
  private var session = None : Option[Session]

  def getSession: Session = {
    if (request.session.isEmpty || request.session.data.get(key).isEmpty) {
      session = Some(Session(Map(key -> createSessionId)))
    } else {
      session = Some(Session(request.session.data))
    }
    session.get
  }

  private def createSessionId: String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    val length = 32
    val sb = new StringBuilder
    for (_ <- 1 to length) {
      val randomNum = util.Random.nextInt(chars.length)
      sb.append(chars(randomNum))
    }
    sb.toString
  }

  def getSessionId: String = {
    session match {
      case None =>
        getSession.data(key)
      case Some(value) =>
        value.data(key)
    }
  }
}
