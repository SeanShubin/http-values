package com.seanshubin.http.values.domain

import org.scalatest.FunSuite

class ContentTypeTest extends FunSuite {
  test("valid content types") {
    assert(ContentType.fromString("aaa/bbb; charset=ccc") === ContentType("aaa/bbb", Some("ccc")))
    assert(ContentType.fromString("aaa/bbb") === ContentType("aaa/bbb", None))
    assert(ContentType.fromString("aaa/bbb;charset=ccc") === ContentType("aaa/bbb", Some("ccc")))
    assert(ContentType.fromString("aaa/bbb ; charset = ccc") === ContentType("aaa/bbb", Some("ccc")))
  }
  test("invalid content types") {
    verifyException(ContentType.fromString("aaa/bbb; foobar=ccc"), "Value 'aaa/bbb; foobar=ccc' does not match pattern '([\\w\\-]+/[\\w\\-]+)\\s*(?:;\\s*charset\\s*=\\s*([\\w\\-]+))?' for content type")
    verifyException(ContentType.fromString("aaabbb; charset=ccc"), "Value 'aaabbb; charset=ccc' does not match pattern '([\\w\\-]+/[\\w\\-]+)\\s*(?:;\\s*charset\\s*=\\s*([\\w\\-]+))?' for content type")
  }

  def verifyException(block: => ContentType, expectedMessage: String) {
    val exception = intercept[RuntimeException] {
      block
    }
    assert(exception.getMessage === expectedMessage)
  }
}
