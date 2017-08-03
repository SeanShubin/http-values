package com.seanshubin.http.values.domain

import java.net.URI
import java.util
import javax.servlet.ServletInputStream

import org.scalatest.FunSuite

import scala.collection.JavaConverters._

class RequestValueTest extends FunSuite {
  test("construct from servlet request") {
    // given
    val stubRequestInfo = StubRequestInfo(
      method = "the-method",
      scheme = "foo",
      remoteUser = "user",
      remoteHost = "example.com",
      remotePort = 8042,
      requestUri = "/over/there",
      queryString = "name=ferret",
      inputStreamText = "Hello, world!",
      headers = Seq(("Content-Type", "text/plain; charset=utf-8"))
    )
    val stubRequest = new StubServletRequest(stubRequestInfo)

    // when
    val requestValue = ServletUtil.readValue(stubRequest)

    // then
    assert(requestValue.method === "the-method")
    assert(requestValue.uri.toString === "foo://user@example.com:8042/over/there?name=ferret")
    assert(requestValue.uri.toUri === new URI("foo://user@example.com:8042/over/there?name=ferret"))
    assert(requestValue.text === "Hello, world!")
    assert(requestValue.maybeContentType === Some(ContentType("text/plain", Some("utf-8"))))
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

  case class StubRequestInfo(method: String,
                             scheme: String,
                             remoteUser: String,
                             remoteHost: String,
                             remotePort: Int,
                             requestUri: String,
                             queryString: String,
                             inputStreamText: String,
                             headers: Seq[(String, String)])

  class StubServletRequest(info: StubRequestInfo) extends HttpServletRequestNotImplemented {
    override def getMethod: String = info.method

    override def getScheme: String = info.scheme

    override def getRemoteUser: String = info.remoteUser

    override def getRemoteHost: String = info.remoteHost

    override def getRemotePort: Int = info.remotePort

    override def getRequestURI: String = info.requestUri

    override def getQueryString: String = info.queryString

    override def getInputStream: ServletInputStream = ServletInputStreamStub.fromText(info.inputStreamText, "utf-8")

    override def getHeaderNames: util.Enumeration[String] = info.headers.map(_._1).iterator.asJavaEnumeration

    override def getHeader(name: String): String = info.headers.toMap.apply(name)
  }

}
