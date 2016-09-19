package pl.hskrk.cc.machines

import java.time.{LocalDate, LocalDateTime}

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.util.Timeout
import pl.hskrk.cc.TwirlSupport
import akka.http.scaladsl.model.StatusCodes.SeeOther
import akka.http.scaladsl.server.{MalformedFormFieldRejection, RejectionHandler}
import pl.hskrk.cc.assets.Assets

import scala.concurrent.duration._

class Machines(system: ActorSystem, implicit val assets: Assets) extends TwirlSupport {

  implicit val timeout: Timeout = 5.seconds

  implicit val executionContext = system.dispatcher

  val machineManager = system.actorOf(Props[MachineManager])

  import akka.pattern.ask
  import MachineManagerProtocol._

  implicit val localDateUnmarshaller = Unmarshaller.strict[String, LocalDate](LocalDate.parse(_))

  val listRoute = Uri.apply("/machines")

  val rejectionHandler = RejectionHandler.newBuilder()
      .handle({
        case rej: MalformedFormFieldRejection =>
          context => context.complete(html.newMachine(Option(s"${rej.fieldName} => ${rej.errorMsg}")))
      }).result()

  val routes = {
    pathPrefix("machines"){
      path("new") {
        complete { html.newMachine() }
      } ~
      get { context =>
        machineManager.ask(SendAllMachines).mapTo[List[Machine]].flatMap { machines =>
          context.complete(html.machinesList(machines))
        }
      } ~
      post {
        handleRejections(rejectionHandler) {
          formFields("name", "purchase-date".as[LocalDate]) { (name, purchaseDate) => context =>
            machineManager ! CreateMachine(name, purchaseDate)
            context.redirect(listRoute, SeeOther)
          }
        }
      }
    } ~
    path("machine" / IntNumber).as(MachineId.apply _) { id =>
      get { context =>
        machineManager.ask(FindById(id)).mapTo[Option[Machine]].flatMap { machineOption =>
          context.complete(machineOption.fold(html.page404())(html.machineDetails.apply))
        }
      } ~
      delete {
        ???
      }
    }
  }
}
