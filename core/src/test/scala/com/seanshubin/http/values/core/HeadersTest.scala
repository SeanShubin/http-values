package com.seanshubin.http.values.core

import org.scalatest.FunSuite

class HeadersTest extends FunSuite {

  import com.seanshubin.http.values.core.Headers._

  test("get no content type") {
    val headers = Map("Connection" -> "keep-alive")
    assert(headers.maybeContentType === None)
  }
  test("get content type") {
    val headers = Map("Content-Type" -> "text/plain")
    assert(headers.maybeContentType === Some(ContentType("text/plain", None)))
  }
  test("get content type and charset") {
    val headers = Map("Content-Type" -> "text/plain; charset=utf-8")
    assert(headers.maybeContentType === Some(ContentType("text/plain", Some("utf-8"))))
  }
  test("set content type") {
    val headers = Map("Connection" -> "keep-alive")
    val actual = headers.setContentType(ContentType("text/plain", None))
    val expected = Map("Connection" -> "keep-alive", "Content-Type" -> "text/plain")
    assert(actual === expected)
  }
  test("set content type and charset") {
    val headers = Map("Connection" -> "keep-alive")
    val actual = headers.setContentType(ContentType("text/plain", Some("utf-8")))
    val expected = Map("Connection" -> "keep-alive", "Content-Type" -> "text/plain; charset=utf-8")
    assert(actual === expected)
  }
  test("update content type") {
    val headers = Map("Connection" -> "keep-alive", "Content-Type" -> "aaa/bbb; charset=ccc")
    val actual = headers.setContentType(ContentType("ddd/eee", None))
    val expected = Map("Connection" -> "keep-alive", "Content-Type" -> "ddd/eee")
    assert(actual === expected)
  }
  test("update content type and charset") {
    val headers = Map("Connection" -> "keep-alive", "Content-Type" -> "aaa/bbb; charset=ccc")
    val actual = headers.setContentType(ContentType("ddd/eee", Some("fff")))
    val expected = Map("Connection" -> "keep-alive", "Content-Type" -> "ddd/eee; charset=fff")
    assert(actual === expected)
  }
}
