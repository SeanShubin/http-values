package com.seanshubin.http.values.core

case class ResponseValue(statusCode: Int, body: Seq[Byte], headers: Map[String, String]) {

  import com.seanshubin.http.values.core.Headers._

  def text: String = {
    maybeCharset match {
      case Some(charset) => new String(body.toArray, charset)
      case None => throw new RuntimeException("Charset must be present in order to get text")
    }
  }

  def maybeCharset: Option[String] = maybeContentType.flatMap(_.maybeCharset)

  def maybeContentType: Option[ContentType] = headers.maybeContentType

  def toMultipleLineString: Seq[String] = {
    Seq(
      s"status code = $statusCode") ++
      bodyToMultipleLineString ++
      headersToMultipleLineString
  }

  def bodyToMultipleLineString: Seq[String] = {
    maybeCharset match {
      case Some(charset) => StringUtil.textToMultipleLineString("body", new String(body.toArray, charset))
      case None => StringUtil.bytesToMultipleLineString("body", body)
    }
  }

  def headersToMultipleLineString: Seq[String] = {
    def headerToString(header: (String, String)): String = {
      val (key, value) = header
      val quotedValue = StringUtil.doubleQuote(value)
      s"$key -> $quotedValue"
    }
    val caption = s"header: ${headers.size} entries"
    caption +: headers.map(headerToString).toSeq.map("  " + _)
  }

}

object ResponseValue {
  def isSuccess(statusCode: Int): Boolean = statusCode >= 200 && statusCode <= 399

  def fromText(statusCode: Int, contentType: ContentType, text: String, headers: Map[String, String]) = {
    val body = text.getBytes(contentType.charset)
    val newHeaders = Headers(headers).setContentType(contentType)
    new ResponseValue(statusCode, body, newHeaders)
  }

  def fromBytes(statusCode: Int, contentType: ContentType, bytes: Seq[Byte], headers: Map[String, String]) = {
    val newHeaders = Headers(headers).setContentType(contentType)
    new ResponseValue(statusCode, bytes, newHeaders)
  }
}
