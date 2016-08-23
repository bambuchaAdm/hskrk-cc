package pl.hskrk.cc

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RejectionHandler
import akka.stream.ActorMaterializer

import scala.io.StdIn

class Server(implicit val system: ActorSystem) {

  implicit val materializer = ActorMaterializer()

  implicit val dispatcher = system.dispatcher

  val logger = Logging(system, getClass)

  val http = Http(system)

  val routes = new HskrkCommandCenter(system)

  def port: Int = 12000 // FIXME move to configuration file (typesafe-config)

//  implicit val rejectionHandler = RejectionHandler {
//
//  }

  def handleSync() : Unit = {
    logger.info("Starting server at port={}", port)
    val future = http.bindAndHandle(routes.route, "localhost", port)
    StdIn.readLine() // Move away somewhere else
    future.flatMap(_.unbind()).onComplete( _ => system.terminate())
  }
}
