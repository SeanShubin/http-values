package com.seanshubin.http.values.domain

import org.scalatest.FunSuite

class ClassLoaderReceiverTest extends FunSuite {
  test("load something") {
    val classLoader = getClass.getClassLoader
    val prefix = "load-from-classpath"
    val contentByExtension = Map(".txt" -> ContentType("text/plain", Some("utf-8")))
    val receiver: Receiver = new ClassLoaderReceiver(classLoader, prefix, contentByExtension, None)
    val request = RequestValue(UriValue.fromString("/hello.txt"), "get", Seq(), Seq())
    val response = receiver.receive(request)
    assert(response.text === "Hello, world!")
    assert(response === ResponseValue(
      200,
      "Hello, world!".getBytes("utf-8"),
      Seq("content-type" -> "text/plain; charset=utf-8")))
  }
}
