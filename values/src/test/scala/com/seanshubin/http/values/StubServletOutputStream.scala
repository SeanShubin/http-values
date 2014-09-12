package com.seanshubin.http.values

import java.io.OutputStream
import javax.servlet.{WriteListener, ServletOutputStream}

class StubServletOutputStream(backingOutputStream: OutputStream) extends ServletOutputStream {
  override def isReady: Boolean = ???

  override def setWriteListener(writeListener: WriteListener): Unit = ???

  override def write(b: Int): Unit = backingOutputStream.write(b)
}
