package com.seanshubin.http.values.domain

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletResponse

import org.scalatest.FunSuite
import org.scalatest.easymock.EasyMockSugar

class ResponseValueTest extends FunSuite with EasyMockSugar {
  test("serialize to servlet response") {
    val httpServletResponse = mock[HttpServletResponse]
    val responseValue = ResponseValue.fromText(200, ContentType("text/plain", Some("utf-8")), "Hello, world!", Seq("header-name" -> "header-value"))
    val backingOutputStream = new ByteArrayOutputStream()
    val fakeServletOutputStream = new StubServletOutputStream(backingOutputStream)
    expecting {
      httpServletResponse.setStatus(200)
      httpServletResponse.getOutputStream.andReturn(fakeServletOutputStream)
      httpServletResponse.setHeader("header-name", "header-value")
      httpServletResponse.setHeader("content-type", "text/plain; charset=utf-8")
    }
    whenExecuting(httpServletResponse) {
      ServletUtil.writeValue(responseValue, httpServletResponse)
      assert(new String(backingOutputStream.toByteArray, "utf-8") === "Hello, world!")
    }
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
}
