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
import pl.hskrk.cc.assets.Assets

import scala.concurrent.Future

class Server(implicit val system: ActorSystem) {

  implicit val materializer = ActorMaterializer()

  implicit val dispatcher = system.dispatcher

  val logger = Logging(system, getClass)

  val http = Http(system)

  val commandCenter = new HskrkCommandCenter(system)

  implicit val rejectionHandler = {
    RejectionHandler.newBuilder()
      .handleNotFound({ context =>
        context.complete(HttpResponse(StatusCodes.NotFound, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,html.page404().toString)))
      }).result()
  }

  def bind() : Future[Http.ServerBinding] = {
    logger.info("Starting server at port={}", commandCenter.httpPort)
    http.bindAndHandle(commandCenter.route, "localhost", commandCenter.httpPort)
  }

  def handle(): Unit ={
    commandCenter.mode.block
  }
}

object Server extends App {
  implicit val system = ActorSystem("hscc")
  implicit val dispatcher = system.dispatcher
  val server = new Server()
  try {
    val future = server.bind()
    server.handle()
    future.flatMap(_.unbind()).onComplete( _ => system.terminate())
  } catch {
    case e: Exception =>
      System.out.println("Exception")
      e.printStackTrace()
      system.terminate()
  }
}