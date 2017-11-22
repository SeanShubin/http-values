package com.seanshubin.http.values.domain

case class ResponseValue(statusCode: Int, body: Seq[Byte], headers: Seq[(String, String)]) {

  def text: String = {
    new String(body.toArray, Headers.fromEntries(headers).effectiveCharset)
  }

  def maybeCharset: Option[String] = maybeContentType.flatMap(_.maybeCharset)

  def maybeContentType: Option[ContentType] = Headers.fromEntries(headers).maybeContentType

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
    caption +: headers.map(headerToString).map("  " + _)
  }

  def withLowerCaseHeaderKeys: ResponseValue = {
    def keyToLowerCase(entry: (String, String)) = {
      val (key, value) = entry
      (key.toLowerCase, value)
    }

    val newHeaders = headers.map(keyToLowerCase)
    copy(headers = newHeaders)
  }
}

object ResponseValue {
  def isSuccess(statusCode: Int): Boolean = statusCode >= 200 && statusCode <= 399

  def fromText(statusCode: Int, contentType: ContentType, text: String, headerEntries: Seq[(String, String)]): ResponseValue = {
    val body = text.getBytes(contentType.charset)
    val newHeaders = Headers.fromEntries(headerEntries).setContentType(contentType)
    new ResponseValue(statusCode, body, newHeaders.entries)
  }

  def fromBytes(statusCode: Int, contentType: ContentType, bytes: Seq[Byte], headerEntries: Seq[(String, String)]): ResponseValue = {
    val newHeaders = Headers.fromEntries(headerEntries).setContentType(contentType)
    new ResponseValue(statusCode, bytes, newHeaders.entries)
  }
}
