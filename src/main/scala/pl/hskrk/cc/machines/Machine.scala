package pl.hskrk.cc.machines

import java.time.LocalDate

import scala.language.implicitConversions

case class MachineId(get: Int) extends AnyVal {
  def isEmpty = get == 0

  def next = MachineId(get + 1)
}

object MachineId {
  implicit def conversionFromInt(int: Int): MachineId = MachineId(int)

  implicit val ordering = Ordering.by[MachineId, Int](_.get)
}

case class Machine(id: MachineId, name: String, purchaseDay: LocalDate) {

}

