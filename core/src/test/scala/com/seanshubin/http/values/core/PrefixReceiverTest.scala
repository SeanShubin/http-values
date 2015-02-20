package com.seanshubin.http.values.core

import org.scalatest.FunSuite
import org.scalatest.mock.EasyMockSugar

class PrefixReceiverTest extends FunSuite with EasyMockSugar {
  test("forward along without prefix") {
    val forwardTo = mock[Receiver]
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val request = RequestValue("/foo/bar/index.html", "get", Seq(), Map())
    val modifiedRequest = RequestValue("/bar/index.html", "get", Seq(), Map())
    val response = ResponseValue(200, Seq(), Map())
    expecting {
      forwardTo.receive(modifiedRequest).andReturn(response)
    }
    whenExecuting(forwardTo) {
      prefixReceiver.receive(request)
    }
  }

  test("rewrite location header") {
    val forwardTo = mock[Receiver]
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val request = RequestValue("/foo/bar/index.html", "get", Seq(), Map())
    val modifiedRequest = RequestValue("/bar/index.html", "get", Seq(), Map())
    val response = ResponseValue(200, Seq(), Map("Location" -> "/bar/error-page.html"))
    val expectedResponse = ResponseValue(200, Seq(), Map("Location" -> "/foo/bar/error-page.html"))
    expecting {
      forwardTo.receive(modifiedRequest).andReturn(response)
    }
    whenExecuting(forwardTo) {
      val modifiedResponse = prefixReceiver.receive(request)
      assert(modifiedResponse === expectedResponse)
    }
  }

  test("handle missing '/' at end") {
    val forwardTo = mock[Receiver]
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val request = RequestValue("/foo", "get", Seq(), Map())
    val modifiedRequest = RequestValue("/", "get", Seq(), Map())
    val response = ResponseValue(200, Seq(), Map())
    expecting {
      forwardTo.receive(modifiedRequest).andReturn(response)
    }
    whenExecuting(forwardTo) {
      prefixReceiver.receive(request)
    }
  }

  test("don't rewrite favicon") {
    val forwardTo = mock[Receiver]
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val request = RequestValue("/favicon.ico", "get", Seq(), Map())
    val response = ResponseValue(200, Seq(), Map())
    expecting {
      forwardTo.receive(request).andReturn(response)
    }
    whenExecuting(forwardTo) {
      prefixReceiver.receive(request)
    }
  }

  test("fail if prefix does not match") {
    val forwardTo = mock[Receiver]
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val request = RequestValue("/foobar", "get", Seq(), Map())
    whenExecuting(forwardTo) {
      val exception = intercept[RuntimeException] {
        prefixReceiver.receive(request)
      }
      assert(exception.getMessage === "Expected uri to start with '/foo', got '/foobar'")
    }
  }
}
