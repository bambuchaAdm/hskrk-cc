package pl.hskrk.cc

import java.util.concurrent.CountDownLatch

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import pl.hskrk.cc.assets.Assets
import pl.hskrk.cc.issues.IssuesModule
import pl.hskrk.cc.machines.Machines

sealed trait Mode {
  def block: Unit
}

object Mode {
  def apply(mode: String): Mode = mode match {
    case "prod" => Production
    case "test" => Test
    case "dev" => Development
    case _ => Development
  }
}

case object Development extends Mode {
  override def block: Unit = {
    println("Development mode of blocking")
    while(System.in.read() != -1){
      //Noop
    }
  }
}
case object Test extends Mode {
  val latch = new CountDownLatch(1)
  override def block: Unit = {
    latch.await()
  }
}
case object Production extends Mode {
  val latch = new CountDownLatch(1)
  override def block: Unit = {
    latch.await()
  }
}

/**
  * Created by bambucha on 21.08.16.
  */
class HskrkCommandCenter(system: ActorSystem) extends TwirlSupport {

  val config = ConfigFactory.load().getConfig("hscc")

  val httpPort  = config.getInt("port")

  val mode = Mode(config.getString("mode"))

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