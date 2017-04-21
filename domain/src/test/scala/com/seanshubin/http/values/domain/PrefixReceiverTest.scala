package com.seanshubin.http.values.domain

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class PrefixReceiverTest extends FunSuite {
  test("forward along without prefix") {
    // given
    val originalRequest = RequestValue("http://www.prefix-receiver.test/foo/bar/index.html", "get", Seq(), Seq())
    val modifiedRequest = RequestValue("http://www.prefix-receiver.test/bar/index.html", "get", Seq(), Seq())
    val originalResponse = ResponseValue(200, Seq(), Seq())
    val forwardTo = new ReceiverStub(modifiedRequest -> originalResponse)
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)

    // when
    val actualResponse = prefixReceiver.receive(originalRequest)

    // then
    assert(actualResponse === originalResponse)
    assert(forwardTo.invocations === Seq(modifiedRequest))
  }

  test("rewrite location header") {
    // given
    val originalRequest = RequestValue("http://www.prefix-receiver.test/foo/bar/index.html", "get", Seq(), Seq())
    val modifiedRequest = RequestValue("http://www.prefix-receiver.test/bar/index.html", "get", Seq(), Seq())
    val originalResponse = ResponseValue(200, Seq(), Seq("location" -> "http://www.prefix-receiver.test/bar/error-page.html"))
    val forwardTo = new ReceiverStub(modifiedRequest -> originalResponse)
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val expectedResponse = ResponseValue(200, Seq(), Seq("location" -> "http://www.prefix-receiver.test/foo/bar/error-page.html"))

    // when
    val actualResponse = prefixReceiver.receive(originalRequest)

    // then
    assert(actualResponse === expectedResponse)
    assert(forwardTo.invocations === Seq(modifiedRequest))
  }

  test("handle missing '/' at end") {
    // given
    val modifiedRequest = RequestValue("http://www.prefix-receiver.test/", "get", Seq(), Seq())
    val originalResponse = ResponseValue(200, Seq(), Seq())
    val forwardTo = new ReceiverStub(modifiedRequest -> originalResponse)
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val originalRequest = RequestValue("http://www.prefix-receiver.test/foo", "get", Seq(), Seq())

    // when
    val actualResponse = prefixReceiver.receive(originalRequest)

    // then
    assert(actualResponse === originalResponse)
    assert(forwardTo.invocations === Seq(modifiedRequest))
  }

  test("don't rewrite favicon") {
    // given
    val originalRequest = RequestValue("http://www.prefix-receiver.test/favicon.ico", "get", Seq(), Seq())
    val originalResponse = ResponseValue(200, Seq(), Seq())
    val forwardTo = new ReceiverStub(originalRequest -> originalResponse)
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)

    // when
    val actualResponse = prefixReceiver.receive(originalRequest)

    // then
    assert(actualResponse === originalResponse)
    assert(forwardTo.invocations === Seq(originalRequest))
  }

  test("fail if prefix does not match") {
    val forwardTo = new ReceiverStub()
    val prefixReceiver = new PrefixReceiver("/foo", forwardTo)
    val request = RequestValue("http://www.prefix-receiver.test/foobar", "get", Seq(), Seq())
    val exception = intercept[RuntimeException] {
      prefixReceiver.receive(request)
    }
    assert(exception.getMessage === "Expected uri to start with '/foo', got '/foobar'")
  }

  class ReceiverStub(results: (RequestValue, ResponseValue)*) extends Receiver {
    val invocations = new ArrayBuffer[RequestValue]()

    override def receive(request: RequestValue): ResponseValue = {
      invocations.append(request)
      results.toSeq.toMap.apply(request)
    }
  }

}
