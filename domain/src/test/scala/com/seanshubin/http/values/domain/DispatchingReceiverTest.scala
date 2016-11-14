package com.seanshubin.http.values.domain

import org.scalatest.FunSuite

class DispatchingReceiverTest extends FunSuite {
  test("no two routes are allowed to have the same name") {
    val dummyReceiver: Receiver = null
    val route = createRoute("route", dummyReceiver, acceptResult = false)
    val exception = intercept[RuntimeException] {
      new DispatchingReceiver(Seq(route, route))
    }
    assert(exception.getMessage === "Duplicate route 'route'")
  }

  test("handle conflict") {
    val dummyReceiver: Receiver = null
    val request = RequestValue("http://uri", "method", Seq(), Seq())
    val routeA = createRoute("route a", dummyReceiver, acceptResult = true)
    val routeB = createRoute("route b", dummyReceiver, acceptResult = true)
    val dispatcher = new DispatchingReceiver(Seq(routeA, routeB))
    val exception = intercept[RuntimeException] {
      dispatcher.receive(request)
    }
    val actual = exception.getMessage
    val expected = "Multiple receivers matched RequestValue(http://uri,method,List(),List()): route a, route b"
    assert(actual === expected)
  }

  test("handle missing") {
    val dummyReceiver: Receiver = null
    val request = RequestValue("http://uri", "method", Seq(), Seq())
    val routeA = createRoute("route a", dummyReceiver, acceptResult = false)
    val routeB = createRoute("route b", dummyReceiver, acceptResult = false)
    val dispatcher = new DispatchingReceiver(Seq(routeA, routeB))
    val exception = intercept[RuntimeException] {
      dispatcher.receive(request)
    }
    assert(exception.getMessage === "No receivers matched RequestValue(http://uri,method,List(),List()): route a, route b")
  }

  test("dispatch through proper route") {
    val dummyReceiver: Receiver = null
    val expectedResponse = ResponseValue(200, Seq(), Seq())
    val request = RequestValue("uri", "method", Seq(), Seq())
    val stubReceiver = new Receiver {
      override def receive(request: RequestValue): ResponseValue = {
        expectedResponse
      }
    }
    val routeA = createRoute("route a", dummyReceiver, acceptResult = false)
    val routeB = createRoute("route b", stubReceiver, acceptResult = true)
    val routeC = createRoute("route c", dummyReceiver, acceptResult = false)
    val dispatcher = new DispatchingReceiver(Seq(routeA, routeB, routeC))
    val actualResponse = dispatcher.receive(request)
    assert(actualResponse === expectedResponse)
  }

  def createRoute(name: String, receiver: Receiver, acceptResult: Boolean): Route = {
    new Route(name, receiver) {
      override def accept(request: RequestValue): Boolean = acceptResult
    }
  }
}
