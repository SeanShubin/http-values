package com.seanshubin.http.values

import java.net.URI

case class RequestValue(uriString: String, method: String, body: Seq[Byte], headers: Map[String, String]) {
  def uri: URI = new URI(uriString)

  def text: String = {
    maybeCharset match {
      case Some(charset) => new String(body.toArray, charset)
      case None => throw new RuntimeException("Charset must be present in order to get text")
    }
  }

  def maybeCharset: Option[String] = maybeContentType.flatMap(_.maybeCharset)

  def maybeContentType: Option[ContentType] = Headers(headers).maybeContentType

  def toMultipleLineString: Seq[String] = {
    Seq(
      s"uri = $uriString",
      s"method = $method") ++
      bodyToMultipleLineString ++
      headersToMultipleLineString
  }

  def bodyToMultipleLineString:Seq[String] = {
    maybeCharset match {
      case Some(charset) => StringUtil.textToMultipleLineString("body", new String(body.toArray, charset))
      case None => StringUtil.bytesToMultipleLineString("body", body)
    }
  }

  def headersToMultipleLineString:Seq[String] = {
    def headerToString(header:(String, String)):String = {
      val (key, value) = header
      val quotedValue = StringUtil.doubleQuote(value)
      s"$key -> $quotedValue"
    }
    val caption = s"header: ${headers.size} entries"
    caption +: headers.map(headerToString).toSeq.map("  " + _)
  }
}

object RequestValue {
  def fromText(uriString: String, method: String, contentType: ContentType, text: String, headers: Map[String, String]) = {
    val body = text.getBytes(contentType.charset)
    val newHeaders = Headers(headers).setContentType(contentType)
    new RequestValue(uriString, method, body, newHeaders)
  }

  def fromBytes(uriString: String, method: String, contentType: ContentType, bytes: Seq[Byte], headers: Map[String, String]) = {
    val newHeaders = Headers(headers).setContentType(contentType)
    new RequestValue(uriString, method, bytes, newHeaders)
  }
}
