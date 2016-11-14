package com.seanshubin.http.values.domain

import org.scalatest.FunSuite

class HeadersTest extends FunSuite {
  test("get no content type") {
    val headers = Headers.fromEntries(Seq("Connection" -> "keep-alive"))
    assert(headers.maybeContentType === None)
  }

  test("get content type") {
    val headers = Headers.fromEntries(Seq("Content-Type" -> "text/plain"))
    assert(headers.maybeContentType === Some(ContentType("text/plain", None)))
  }

  test("get content type and charset") {
    val headers = Headers.fromEntries(Seq("Content-Type" -> "text/plain; charset=utf-8"))
    assert(headers.maybeContentType === Some(ContentType("text/plain", Some("utf-8"))))
  }

  test("content type is case insensitive") {
    val headers1 = Headers.fromEntries(Seq("Content-Type" -> "text/plain; charset=utf-8"))
    val headers2 = Headers.fromEntries(Seq("content-type" -> "text/plain; charset=utf-8"))
    val headers3 = Headers.fromEntries(Seq("CONTENT-TYPE" -> "text/plain; charset=utf-8"))
    assert(headers1.maybeContentType === Some(ContentType("text/plain", Some("utf-8"))))
    assert(headers2.maybeContentType === Some(ContentType("text/plain", Some("utf-8"))))
    assert(headers3.maybeContentType === Some(ContentType("text/plain", Some("utf-8"))))
  }

  test("set content type") {
    val headers = Headers.fromEntries(Seq("Connection" -> "keep-alive"))
    val actual = headers.setContentType(ContentType("text/plain", None))
    val expected = Headers.fromEntries(Seq("connection" -> "keep-alive", "content-type" -> "text/plain"))
    assert(actual === expected)
  }

  test("set content type and charset") {
    val headers = Headers.fromEntries(Seq("Connection" -> "keep-alive"))
    val actual = headers.setContentType(ContentType("text/plain", Some("utf-8")))
    val expected = Headers.fromEntries(Seq("connection" -> "keep-alive", "content-type" -> "text/plain; charset=utf-8"))
    assert(actual === expected)
  }

  test("update content type") {
    val headers = Headers.fromEntries(Seq("Connection" -> "keep-alive", "Content-Type" -> "aaa/bbb; charset=ccc"))
    val actual = headers.setContentType(ContentType("ddd/eee", None))
    val expected = Headers.fromEntries(Seq("connection" -> "keep-alive", "content-type" -> "ddd/eee"))
    assert(actual === expected)
  }

  test("update content type and charset") {
    val headers = Headers.fromEntries(Seq("Connection" -> "keep-alive", "content-type" -> "aaa/bbb; charset=ccc"))
    val actual = headers.setContentType(ContentType("ddd/eee", Some("fff")))
    val expected = Headers.fromEntries(Seq("connection" -> "keep-alive", "content-type" -> "ddd/eee; charset=fff"))
    assert(actual === expected)
  }
}
