package com.seanshubin.http.values.domain

case class RequestValue(uri: UriValue, method: String, body: Seq[Byte], headers: Seq[(String, String)]) {
  def text: String = {
    new String(body.toArray, Headers.fromEntries(headers).effectiveCharset)
  }

  def maybeCharset: Option[String] = maybeContentType.flatMap(_.maybeCharset)

  def maybeContentType: Option[ContentType] = Headers(headers).maybeContentType

  def toMultipleLineString: Seq[String] = {
    Seq(
      s"uri = $uri",
      s"method = $method") ++
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
    caption +: headers.map(headerToString).map("  " + _)
  }
}

object RequestValue {
  def fromText(uri: UriValue, method: String, contentType: ContentType, text: String, headerEntries: Seq[(String, String)]): RequestValue = {
    val body = text.getBytes(contentType.charset)
    val newHeaders = Headers(headerEntries).setContentType(contentType)
    new RequestValue(uri, method, body, newHeaders.entries)
  }

  def fromText(uriString: String, method: String, contentType: ContentType, text: String, headerEntries: Seq[(String, String)]): RequestValue = {
    fromText(UriValue.fromString(uriString), method, contentType, text, headerEntries)
  }

  def fromBytes(uri: UriValue, method: String, contentType: ContentType, bytes: Seq[Byte], headerEntries: Seq[(String, String)]): RequestValue = {
    val newHeaders = Headers(headerEntries).setContentType(contentType)
    new RequestValue(uri, method, bytes, newHeaders.entries)
  }

  def fromBytes(uriString: String, method: String, contentType: ContentType, bytes: Seq[Byte], headerEntries: Seq[(String, String)]): RequestValue = {
    fromBytes(UriValue.fromString(uriString), method, contentType, bytes, headerEntries)
  }

  def apply(uriString: String, method: String, body: Seq[Byte], headers: Seq[(String, String)]): RequestValue = {
    RequestValue(UriValue.fromString(uriString), method, body, headers)
  }
}
