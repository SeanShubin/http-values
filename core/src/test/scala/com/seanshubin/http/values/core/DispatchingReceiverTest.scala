package com.seanshubin.http.values.core

import org.scalatest.FunSuite

class DispatchingReceiverTest extends FunSuite {
  test("no two gates are allowed to have the same name") {
    val dummyReceiver: Receiver = null
    val gate = createGate("gate", dummyReceiver, acceptResult = false)
    val exception = intercept[RuntimeException] {
      new DispatchingReceiver(Seq(gate, gate))
    }
    assert(exception.getMessage === "Duplicate gate 'gate'")
  }

  test("handle conflict") {
    val dummyReceiver: Receiver = null
    val request = RequestValue("uri", "method", Seq(), Map())
    val gateA = createGate("gate a", dummyReceiver, acceptResult = true)
    val gateB = createGate("gate b", dummyReceiver, acceptResult = true)
    val dispatcher = new DispatchingReceiver(Seq(gateA, gateB))
    val exception = intercept[RuntimeException] {
      dispatcher.receive(request)
    }
    assert(exception.getMessage === "Multiple receivers matched RequestValue(uri,method,List(),Map()): gate a, gate b")
  }

  test("handle missing") {
    val dummyReceiver: Receiver = null
    val request = RequestValue("uri", "method", Seq(), Map())
    val gateA = createGate("gate a", dummyReceiver, acceptResult = false)
    val gateB = createGate("gate b", dummyReceiver, acceptResult = false)
    val dispatcher = new DispatchingReceiver(Seq(gateA, gateB))
    val exception = intercept[RuntimeException] {
      dispatcher.receive(request)
    }
    assert(exception.getMessage === "No receivers matched RequestValue(uri,method,List(),Map()): gate a, gate b")
  }

  test("dispatch through proper gate") {
    val dummyReceiver: Receiver = null
    val expectedResponse = ResponseValue(200, Seq(), Map())
    val request = RequestValue("uri", "method", Seq(), Map())
    val stubReceiver = new Receiver {
      override def receive(request: RequestValue): ResponseValue = {
        expectedResponse
      }
    }
    val gateA = createGate("gate a", dummyReceiver, acceptResult = false)
    val gateB = createGate("gate b", stubReceiver, acceptResult = true)
    val gateC = createGate("gate c", dummyReceiver, acceptResult = false)
    val dispatcher = new DispatchingReceiver(Seq(gateA, gateB, gateC))
    val actualResponse = dispatcher.receive(request)
    assert(actualResponse === expectedResponse)
  }

  def createGate(name: String, receiver: Receiver, acceptResult: Boolean): Gate = {
    new Gate(name, receiver) {
      override def accept(request: RequestValue): Boolean = acceptResult
    }
  }
}
