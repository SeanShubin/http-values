package com.seanshubin.http.values

import java.io._

import scala.annotation.tailrec

object IoUtil {
  def feedInputStreamToOutputStream(inputStream: InputStream, outputStream: OutputStream) {
    @tailrec
    def loop(byte: Int) {
      if (byte != -1) {
        outputStream.write(byte)
        loop(inputStream.read())
      }
    }
    loop(inputStream.read())
  }

  def feedReaderToWriter(reader: Reader, writer: Writer) {
    @tailrec
    def loop(char: Int) {
      if (char != -1) {
        writer.write(char)
        loop(reader.read())
      }
    }
    loop(reader.read())
  }

  def inputStreamToBytes(inputStream: InputStream): Array[Byte] = {
    val outputStream = new ByteArrayOutputStream
    feedInputStreamToOutputStream(inputStream, outputStream)
    val byteArray = outputStream.toByteArray
    byteArray
  }

  def bytesToOutputStream(bytes: Array[Byte], outputStream: OutputStream): Unit = {
    val inputStream = new ByteArrayInputStream(bytes)
    feedInputStreamToOutputStream(inputStream, outputStream)
  }

  def readerToString(reader: Reader): String = {
    val writer = new StringWriter()
    feedReaderToWriter(reader, writer)
    val string = writer.toString
    string
  }
}
