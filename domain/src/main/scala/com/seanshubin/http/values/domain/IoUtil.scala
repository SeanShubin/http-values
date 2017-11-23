package com.seanshubin.http.values.domain

import java.io._
import java.nio.charset.Charset

import scala.annotation.tailrec

object IoUtil {
  @tailrec
  def feedInputStreamToOutputStream(inputStream: InputStream, outputStream: OutputStream) {
    val byte = inputStream.read()
    if (byte != -1) {
      outputStream.write(byte)
      feedInputStreamToOutputStream(inputStream, outputStream)
    }
  }

  @tailrec
  def feedReaderToWriter(reader: Reader, writer: Writer) {
    val char = reader.read()
    if (char != -1) {
      writer.write(char)
      feedReaderToWriter(reader, writer)
    }
  }

  def inputStreamToBytes(inputStream: InputStream): Array[Byte] = {
    val outputStream = new ByteArrayOutputStream
    feedInputStreamToOutputStream(inputStream, outputStream)
    val byteArray = outputStream.toByteArray
    byteArray
  }

  def readerToString(reader: Reader): String = {
    val writer = new StringWriter()
    feedReaderToWriter(reader, writer)
    val string = writer.toString
    string
  }

  def bytesToInputStream(bytes: Array[Byte]): InputStream = {
    new ByteArrayInputStream(bytes)
  }

  def stringToInputStream(s: String, charset: Charset): InputStream = {
    bytesToInputStream(s.getBytes(charset))
  }

  def inputStreamToString(inputStream: InputStream, charset: Charset): String = {
    val bytes = inputStreamToBytes(inputStream)
    new String(bytes, charset)
  }

  def stringToReader(s: String): Reader = {
    new StringReader(s)
  }

  def stringToOutputStream(s: String, charset: Charset, outputStream: OutputStream): Unit = {
    outputStream.write(s.getBytes(charset))
  }

  def bytesToString(bytes: Array[Byte], charset: Charset): String = new String(bytes, charset)
}
