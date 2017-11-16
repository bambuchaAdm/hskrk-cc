package pl.hskrk.cc

import akka.actor.ActorSystem

import scala.concurrent.Await
import scala.io.StdIn
import scala.concurrent.duration._
import scala.util.control.NonFatal

object DevServer extends App {
  implicit val system = ActorSystem("test-app")
  try {
    val server = new Server()
    server.bind()
    server.commandCenter.mode.block
  } catch {
    case e if NonFatal(e) =>
      Await.ready(system.terminate(), 1.minute)
  }
}
