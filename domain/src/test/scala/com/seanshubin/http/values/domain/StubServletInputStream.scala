package com.seanshubin.http.values.domain

import java.io.{ByteArrayInputStream, InputStream}
import javax.servlet.{ReadListener, ServletInputStream}

class StubServletInputStream(backingInputStream: InputStream) extends ServletInputStream {
  override def isFinished: Boolean = ???

  override def isReady: Boolean = ???

  override def setReadListener(readListener: ReadListener): Unit = ???

  override def read(): Int = backingInputStream.read()
}

object StubServletInputStream {
  def fromText(text: String, charsetName: String) = fromBytes(text.getBytes(charsetName))

  def fromBytes(bytes: Array[Byte]) = new StubServletInputStream(new ByteArrayInputStream(bytes))
}
