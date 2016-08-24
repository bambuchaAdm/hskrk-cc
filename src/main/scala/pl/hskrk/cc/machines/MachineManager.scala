package pl.hskrk.cc.machines

import java.time.{Clock, Instant, LocalDate}

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive


object MachineManagerProtocol {
  object SendAllMachines
  case class FindById(id: MachineId)
  case class CreateMachine(name: String, purchaseDate: LocalDate)
}

class MachineManager extends Actor {

  import MachineManagerProtocol._

  var machines = {
    val now = LocalDate.now()
    List(
      Machine(1,"Szlifierka", now.minusDays(1)),
      Machine(2,"Tokarka", now.minusDays(2)),
      Machine(3,"Lutownica", now.minusDays(3)),
      Machine(4,"Kompresor", now.minusDays(4)),
      Machine(5,"Drukarka 3D", now.minusDays(5))
    )
  }

  override def receive: Receive = {
    case SendAllMachines =>
      sender() ! machines
    case FindById(id) =>
      sender ! machines.find(_.id == id)
    case CreateMachine(name, purchaseDate) =>
      val maxId = machines.map(_.id).max
      val newMachine= Machine(maxId.next, name, purchaseDate)
      machines = machines :+ newMachine
  }
}
