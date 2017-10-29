package com.seanshubin.http.values.domain

import java.io.{ByteArrayInputStream, InputStream}
import javax.servlet.{ReadListener, ServletInputStream}

class ServletInputStreamStub(backingInputStream: InputStream) extends ServletInputStream {
  override def isFinished: Boolean = ???

  override def isReady: Boolean = ???

  override def setReadListener(readListener: ReadListener): Unit = ???

  override def read(): Int = backingInputStream.read()
}

object ServletInputStreamStub {
  def fromText(text: String, charsetName: String) = fromBytes(text.getBytes(charsetName))

  def fromBytes(bytes: Array[Byte]) = new ServletInputStreamStub(new ByteArrayInputStream(bytes))
}
