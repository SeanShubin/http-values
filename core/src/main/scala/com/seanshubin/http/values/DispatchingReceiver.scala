package com.seanshubin.http.values

class DispatchingReceiver(gates: Seq[Gate]) extends Receiver {
  SequenceUtil.searchForFirstDuplicate(gates.map(_.name)) match {
    case Some(name) => throw new RuntimeException(s"Duplicate gate '$name'")
    case None =>
  }

  override def receive(request: RequestValue): ResponseValue = {
    def isMatchingGate(gate: Gate) = gate.accept(request)
    val matchingGates = gates.filter(isMatchingGate)
    if (matchingGates.size == 1) matchingGates.head.receiver.receive(request)
    else if (matchingGates.size == 0) {
      val gateNames = gates.map(_.name).mkString(", ")
      throw new RuntimeException(s"No receivers matched $request: $gateNames")
    } else {
      val gateNames = matchingGates.map(_.name).mkString(", ")
      throw new RuntimeException(s"Multiple receivers matched $request: $gateNames")
    }
  }
}
