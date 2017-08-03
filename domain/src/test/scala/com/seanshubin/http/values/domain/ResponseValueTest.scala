package com.seanshubin.http.values.domain

import java.nio.charset.StandardCharsets
import javax.servlet.ServletOutputStream

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class ResponseValueTest extends FunSuite {
  test("serialize to servlet response") {
    // given
    val httpServletResponse = new HttpServletResponseStub
    val responseValue = ResponseValue.fromText(200, ContentType("text/plain", Some("utf-8")), "Hello, world!", Seq("header-name" -> "header-value"))

    // when
    ServletUtil.writeValue(responseValue, httpServletResponse)

    // then
    assert(httpServletResponse.statusCode === 200)
    assert(httpServletResponse.headers === Seq(
      "header-name" -> "header-value",
      "content-type" -> "text/plain; charset=utf-8"))
    assert(httpServletResponse.outputStream.asUtf8 === "Hello, world!")
  }

  test("text response value as text") {
    val responseValue = ResponseValue(200, "Hello, world!".getBytes(StandardCharsets.UTF_8), Seq("Content-Type" -> "text/plain; charset=utf-8"))
    val actual = responseValue.toMultipleLineString
    val expected = Seq(
      "status code = 200",
      "body: 1 lines",
      "  \"Hello, world!\"",
      "header: 1 entries",
      "  Content-Type -> \"text/plain; charset=utf-8\""
    )
    assert(actual === expected)
  }

  test("binary response value as text") {
    val responseValue = ResponseValue(200, "Hello, world!".getBytes(StandardCharsets.UTF_8), Seq())
    val actual = responseValue.toMultipleLineString
    val expected = Seq(
      "status code = 200",
      "body: 13 bytes",
      "  48 65 6c 6c 6f 2c 20 77 6f 72 6c 64 21           Hello, world!",
      "header: 0 entries"
    )
    assert(actual === expected)
  }

  class HttpServletResponseStub extends HttpServletResponseNotImplemented {
    var statusCode: Int = -1
    val headers = new ArrayBuffer[(String, String)]()
    val outputStream = new ServletOutputStreamStub

    override def setStatus(sc: Int): Unit = statusCode = sc

    override def setHeader(name: String, value: String): Unit = headers.append((name, value))

    override def getOutputStream: ServletOutputStream = outputStream
  }

}
