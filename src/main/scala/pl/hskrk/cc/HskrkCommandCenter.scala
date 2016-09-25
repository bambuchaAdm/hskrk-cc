package pl.hskrk.cc

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.MissingCookieRejection
import com.typesafe.config.ConfigFactory
import pl.hskrk.cc.assets.Assets
import pl.hskrk.cc.issues.IssuesModule
import pl.hskrk.cc.machines.Machines

/**
  * Created by bambucha on 21.08.16.
  */
class HskrkCommandCenter(system: ActorSystem) extends TwirlSupport {

  val config = ConfigFactory.load().getConfig("hscc")

  implicit val assets = new Assets(config.getString("assets.path"))

  val machines = new Machines(system, assets)

  val issues = new IssuesModule(system, assets)


  val route = logRequestResult("ALL"){
    pathSingleSlash {
      get {
        complete(html.index(assets))
      }
    } ~
    machines.routes ~
    issues.routes ~
    assets.routes
  }
}
