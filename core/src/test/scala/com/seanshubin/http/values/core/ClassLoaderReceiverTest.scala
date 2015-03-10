package com.seanshubin.http.values.core

import org.scalatest.FunSuite

class ClassLoaderReceiverTest extends FunSuite {
  test("load something") {
    val classLoader = getClass.getClassLoader
    val prefix = "load-from-classpath"
    val contentByExtension = Map(".txt" -> ContentType("text/plain", Some("utf-8")))
    val receiver: Receiver = new ClassLoaderReceiver(classLoader, prefix, contentByExtension, None)
    val request = RequestValue("/hello.txt", "get", Seq(), Map())
    val response = receiver.receive(request)
    assert(response.text === "Hello, world!")
    assert(response === ResponseValue(
      200,
      "Hello, world!".getBytes("utf-8"),
      Map("content-type" -> "text/plain; charset=utf-8")))
  }
}
