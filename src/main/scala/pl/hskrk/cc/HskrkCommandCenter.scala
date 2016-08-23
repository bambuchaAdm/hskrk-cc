package pl.hskrk.cc

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import pl.hskrk.cc.assets.Assets
import pl.hskrk.cc.machines.Machines

/**
  * Created by bambucha on 21.08.16.
  */
class HskrkCommandCenter(system: ActorSystem) extends TwirlSupport {

  val machines = new Machines(system)

  val route = logRequestResult("ALL"){
    pathSingleSlash {
      get {
        complete(html.index())
      }
    } ~
    machines.routes ~
    path("issues") {
      get {
        ???
      } ~
      post {
        ???
      }
    } ~
    path("issue" ~ IntNumber){ id =>
      path("comments") {
        post {
          ???
        }
      } ~
      get {
        ???
      } ~
      put {
        ???
      } ~
      delete {
        ???
      }
    } ~ { Assets.routes  } ~
    complete(html.page404())

  }
}
