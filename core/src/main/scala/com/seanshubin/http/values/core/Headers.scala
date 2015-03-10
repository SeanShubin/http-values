package com.seanshubin.http.values.core

case class Headers(caseSensitive: Map[String, String]) {
  private val headers = for { (key, value) <- caseSensitive} yield (key.toLowerCase, value)
  def maybeContentType: Option[ContentType] = {
    headers.get("content-type").map(ContentType.fromString)
  }

  def setContentType(contentType: ContentType): Map[String, String] = {
    headers + ("content-type" -> contentType.toString)
  }

  def effectiveCharset: String = {
    maybeContentType match {
      case Some(ContentType(_, Some(charset))) => charset
      case _ => "ISO-8859-1"
    }
  }
}

object Headers {

  import scala.language.implicitConversions

  implicit def toHeaders(headers: Map[String, String]):Headers = new Headers(headers)
}
