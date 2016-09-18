package pl.hskrk.cc.users

import akka.actor.Actor
import akka.actor.Actor.Receive

object USerManagerProtocol {
  case class FindUser(id: UserId)
}

class UserManager extends Actor {

  import USerManagerProtocol._

  val users = List(
    User(UserId(1), "Temporal"),
    User(UserId(2), "Bambucha"),
    User(UserId(3), "Przykładowy użytkownik")
  )

  override def receive: Receive = {
    case FindUser(id) =>
      sender() ! users.find(_.id == id)
  }
}

object UserManager {
  val example = User(UserId(3), "Example")
}