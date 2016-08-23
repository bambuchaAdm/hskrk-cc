package pl.hskrk.cc.machines

import scala.language.implicitConversions

case class MachineId(get: Int) extends AnyVal {
  def isEmpty = false
}

object MachineId {
  implicit def conversionFromInt(int: Int): MachineId = MachineId(int)
}

case class Machine(id: MachineId, name: String) {

}

