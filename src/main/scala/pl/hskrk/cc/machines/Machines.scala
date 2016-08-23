package pl.hskrk.cc.machines

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import pl.hskrk.cc.TwirlSupport
import pl.hskrk.cc.machines.MachineManagerProtocol.SendAllMachines
import scala.concurrent.duration._

class Machines(system: ActorSystem) extends TwirlSupport {

  implicit val timeout: Timeout = 5.seconds

  implicit val executionContext = system.dispatcher

  val machineManager = system.actorOf(Props[MachineManager])

  import akka.pattern.ask

  val routes = {
    path("machines"){
      get { context =>
        machineManager.ask(SendAllMachines).mapTo[List[Machine]].flatMap { machines =>
          context.complete(html.machines(machines))
        }
      } ~
      post {
        ???
      }
    } ~
    path("machine" / IntNumber) { id =>
      get {
        complete {
          html.machineDetails(Machine(1,"Śrubowkręt"))
        }
      } ~
      delete {
        ???
      }
    }
  }

}
