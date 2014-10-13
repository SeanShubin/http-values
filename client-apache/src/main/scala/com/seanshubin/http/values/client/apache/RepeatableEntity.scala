package com.seanshubin.http.values.client.apache

import java.io.{ByteArrayInputStream, InputStream, OutputStream}

import org.apache.http.entity.AbstractHttpEntity

class RepeatableEntity(bytes: Seq[Byte]) extends AbstractHttpEntity {
  override def isRepeatable: Boolean = true

  override def isStreaming: Boolean = false

  override def writeTo(outputStream: OutputStream): Unit = outputStream.write(bytes.toArray)

  override def getContent: InputStream = new ByteArrayInputStream(bytes.toArray)

  override def getContentLength: Long = bytes.length
}
