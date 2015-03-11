package com.seanshubin.http.values.core

import org.scalatest.FunSuite
import org.scalatest.mock.EasyMockSugar

class PrefixReceiverTest extends FunSuite with EasyMockSugar {
  test("forward along without prefix") {
    val forwardTo = mock[Receiver]
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val request = RequestValue("/foo/bar/index.html", "get", Seq(), Seq())
    val modifiedRequest = RequestValue("/bar/index.html", "get", Seq(), Seq())
    val response = ResponseValue(200, Seq(), Seq())
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
    val request = RequestValue("/foo/bar/index.html", "get", Seq(), Seq())
    val modifiedRequest = RequestValue("/bar/index.html", "get", Seq(), Seq())
    val response = ResponseValue(200, Seq(), Seq("location" -> "/bar/error-page.html"))
    val expectedResponse = ResponseValue(200, Seq(), Seq("location" -> "/foo/bar/error-page.html"))
    expecting {
      forwardTo.receive(modifiedRequest).andReturn(response)
    }
    whenExecuting(forwardTo) {
      val modifiedResponse = prefixReceiver.receive(request)
      modifiedResponse.toMultipleLineString.foreach(println)
      expectedResponse.toMultipleLineString.foreach(println)
      assert(modifiedResponse === expectedResponse)
    }
  }

  test("handle missing '/' at end") {
    val forwardTo = mock[Receiver]
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val request = RequestValue("/foo", "get", Seq(), Seq())
    val modifiedRequest = RequestValue("/", "get", Seq(), Seq())
    val response = ResponseValue(200, Seq(), Seq())
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
    val request = RequestValue("/favicon.ico", "get", Seq(), Seq())
    val response = ResponseValue(200, Seq(), Seq())
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
    val request = RequestValue("/foobar", "get", Seq(), Seq())
    whenExecuting(forwardTo) {
      val exception = intercept[RuntimeException] {
        prefixReceiver.receive(request)
      }
      assert(exception.getMessage === "Expected uri to start with '/foo', got '/foobar'")
    }
  }
}
