package com.seanshubin.http.values.core

import java.net.URI
import javax.servlet.http.HttpServletRequest

import org.scalatest.FunSuite
import org.scalatest.mock.EasyMockSugar

import scala.collection.JavaConversions

class RequestValueTest extends FunSuite with EasyMockSugar {
  test("construct from servlet request") {
    val httpServletRequest = mock[HttpServletRequest]
    expecting {
      httpServletRequest.getMethod.andReturn("the-method")
      httpServletRequest.getRequestURI.andReturn("foo://user@example.com:8042/over/there?name=ferret#nose")
      httpServletRequest.getInputStream.andReturn(StubServletInputStream.fromText("Hello, world!", "utf-8"))
      httpServletRequest.getHeaderNames.andReturn(JavaConversions.asJavaEnumeration(Seq("Content-Type").iterator))
      httpServletRequest.getHeader("Content-Type").andReturn("text/plain; charset=utf-8")
    }
    whenExecuting(httpServletRequest) {
      val requestValue = ServletUtil.readValue(httpServletRequest)
      assert(requestValue.method === "the-method")
      assert(requestValue.uriString === "foo://user@example.com:8042/over/there?name=ferret#nose")
      assert(requestValue.uri === new URI("foo://user@example.com:8042/over/there?name=ferret#nose"))
      assert(requestValue.text === "Hello, world!")
      assert(requestValue.maybeContentType === Some(ContentType("text/plain", Some("utf-8"))))
    }
  }

  test("different body types") {
    assert(
      RequestValue.fromText(
        "some uri",
        "some method",
        ContentType("content/type", Some("utf-8")),
        "some text",
        Map("header key" -> "header value")) ===
        RequestValue(
          "some uri",
          "some method",
          "some text".getBytes("utf-8").toSeq,
          Map(
            "header key" -> "header value",
            "Content-Type" -> "content/type; charset=utf-8")))
    assert(
      RequestValue.fromBytes(
        "some uri",
        "some method",
        ContentType("binary/type", None),
        Seq(1, 2, 3),
        Map("header key" -> "header value")) ===
        RequestValue(
          "some uri",
          "some method",
          Seq(1, 2, 3),
          Map(
            "header key" -> "header value",
            "Content-Type" -> "binary/type")))
  }

  test("character encoding from headers") {
    val noContentType = Map("Some-Header" -> "text/html; charset=utf-8")
    val notSpecified = Map("Content-Type" -> "text/html")
    val specified = Map("Content-Type" -> "text/html; charset=utf-8")
    assert(Headers(notSpecified).effectiveCharset === "ISO-8859-1")
    assert(Headers(specified).effectiveCharset === "utf-8")
    assert(Headers(noContentType).effectiveCharset === "ISO-8859-1")
  }

  test("can't get text without charset") {
    val headers = Map("Content-Type" -> "content/type")
    val requestValue = new RequestValue("some uri", "some method", "hello".getBytes("utf-8"), headers)
    val exception = intercept[RuntimeException] {
      requestValue.text
    }
    assert(exception.getMessage === "Charset must be present in order to get text")
  }
}
