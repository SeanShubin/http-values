package com.seanshubin.http.values.domain

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

import com.seanshubin.http.values.domain.IoUtil._
import org.scalatest.FunSuite

class IoUtilTest extends FunSuite {
  val charset = StandardCharsets.UTF_8
  test("bytes") {
    val inputStream = stringToInputStream("Hello, world!", charset)
    val string = inputStreamToString(inputStream, charset)
    assert(string === "Hello, world!")
  }

  test("string to output stream") {
    val original = "Hello, world!"
    val outputStream = new ByteArrayOutputStream()
    IoUtil.stringToOutputStream(original, charset, outputStream)
    val string = IoUtil.bytesToString(outputStream.toByteArray, charset)
    assert(string === "Hello, world!")
  }


  test("chars") {
    val reader = stringToReader("Hello, world!")
    val string = readerToString(reader)
    assert(string === "Hello, world!")
  }
}
