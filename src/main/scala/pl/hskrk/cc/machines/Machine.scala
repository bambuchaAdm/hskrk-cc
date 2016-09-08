package pl.hskrk.cc.machines

import java.time.LocalDate

import scala.language.implicitConversions

case class MachineId(get: Int) extends AnyVal {
  def isEmpty = get == 0
  def next = MachineId(get + 1)
}

object MachineId {
  val empty = MachineId(0)

  implicit val ordering = Ordering.by[MachineId, Int](_.get)
}

case class Machine(id: MachineId, name: String, purchaseDay: LocalDate) {

}

object Machine {
  def apply(name: String, purchaseDay: LocalDate): Machine = Machine(MachineId.empty, name, purchaseDay)
}

