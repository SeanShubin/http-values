package com.seanshubin.http.values.domain

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import javax.servlet.{ServletOutputStream, WriteListener}

class ServletOutputStreamStub extends ServletOutputStream {
  val byteArrayOutputStream = new ByteArrayOutputStream()
  def asUtf8:String = new String(byteArrayOutputStream.toByteArray, StandardCharsets.UTF_8)
  override def isReady: Boolean = ???

  override def setWriteListener(writeListener: WriteListener): Unit = ???

  override def write(b: Int): Unit = byteArrayOutputStream.write(b)
}
