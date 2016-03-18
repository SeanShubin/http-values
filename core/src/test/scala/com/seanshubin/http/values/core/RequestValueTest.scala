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
      httpServletRequest.getScheme.andReturn("foo")
      httpServletRequest.getRemoteUser.andReturn("user")
      httpServletRequest.getRemoteHost.andReturn("example.com")
      httpServletRequest.getRemotePort.andReturn(8042)
      httpServletRequest.getRequestURI.andReturn("/over/there")
      httpServletRequest.getQueryString.andReturn("name=ferret")
      httpServletRequest.getInputStream.andReturn(StubServletInputStream.fromText("Hello, world!", "utf-8"))
      httpServletRequest.getHeaderNames.andReturn(JavaConversions.asJavaEnumeration(Seq("Content-Type").iterator))
      httpServletRequest.getHeader("Content-Type").andReturn("text/plain; charset=utf-8")
    }
    whenExecuting(httpServletRequest) {
      val requestValue = ServletUtil.readValue(httpServletRequest)
      assert(requestValue.method === "the-method")
      assert(requestValue.uri.toString === "foo://user@example.com:8042/over/there?name=ferret")
      assert(requestValue.uri.toUri === new URI("foo://user@example.com:8042/over/there?name=ferret"))
      assert(requestValue.text === "Hello, world!")
      assert(requestValue.maybeContentType === Some(ContentType("text/plain", Some("utf-8"))))
    }
  }

  test("different body types") {
    assert(
      RequestValue.fromText(
        "some-uri",
        "some method",
        ContentType("content/type", Some("utf-8")),
        "some text",
        Seq("header key" -> "header value")) ===
        RequestValue(
          "some-uri",
          "some method",
          "some text".getBytes("utf-8").toSeq,
          Seq(
            "header key" -> "header value",
            "content-type" -> "content/type; charset=utf-8")))
    assert(
      RequestValue.fromBytes(
        "some-uri",
        "some method",
        ContentType("binary/type", None),
        Seq[Byte](1, 2, 3),
        Seq("header key" -> "header value")) ===
        RequestValue(
          "some-uri",
          "some method",
          Seq[Byte](1, 2, 3),
          Seq(
            "header key" -> "header value",
            "content-type" -> "binary/type")))
  }

  test("character encoding from headers") {
    val noContentType = Seq("Some-Header" -> "text/html; charset=utf-8")
    val notSpecified = Seq("Content-Type" -> "text/html")
    val specified = Seq("Content-Type" -> "text/html; charset=utf-8")
    assert(Headers.fromEntries(notSpecified).effectiveCharset === "ISO-8859-1")
    assert(Headers.fromEntries(specified).effectiveCharset === "utf-8")
    assert(Headers.fromEntries(noContentType).effectiveCharset === "ISO-8859-1")
  }

  test("can't get text without charset") {
    val headers = Seq("Content-Type" -> "content/type")
    val requestValue = RequestValue("some-uri", "some method", "hello".getBytes("utf-8"), headers)
    val exception = intercept[RuntimeException] {
      requestValue.text
    }
    assert(exception.getMessage === "Charset must be present in order to get text")
  }
}
