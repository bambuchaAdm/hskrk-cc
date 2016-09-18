package pl.hskrk.cc

import java.util.concurrent.CountDownLatch

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{MalformedFormFieldRejection, RejectionHandler}
import akka.stream.ActorMaterializer

import scala.concurrent.{Future, Promise}
import scala.io.StdIn

class Server(implicit val system: ActorSystem) extends TwirlSupport  { // Remove support later

  implicit val materializer = ActorMaterializer()

  implicit val dispatcher = system.dispatcher

  val logger = Logging(system, getClass)

  val http = Http(system)

  val routes = new HskrkCommandCenter(system)

  def port: Int = 12000 // FIXME move to configuration file (typesafe-config)

  implicit val rejectionHandler = {

    RejectionHandler.newBuilder()
      .handleNotFound({ context =>
        context.complete(HttpResponse(StatusCodes.NotFound, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,html.page404().toString)))
      }).result()
  }

  val counter = new CountDownLatch(1)

  def handleSync() : Unit = {
    logger.info("Starting server at port={}", port)
    val future = http.bindAndHandle(routes.route, "localhost", port)
    counter.await()
    future.flatMap(_.unbind()).onComplete( _ => system.terminate())
  }
}
