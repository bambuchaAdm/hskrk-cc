package pl.hskrk.cc.machines

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive


object MachineManagerProtocol {
  object SendAllMachines
}

class MachineManager extends Actor {

  import MachineManagerProtocol._

  // For fast development only. Changed later to database call or maybe not ?
  val machines = List(
    Machine(1,"Szlifierka"),
    Machine(2,"Tokarka"),
    Machine(3,"Lutownica"),
    Machine(4,"Kompresor"),
    Machine(5,"Drukarka 3D")
  )

  override def receive: Receive = {
    case SendAllMachines =>
      sender() ! machines
  }
}
