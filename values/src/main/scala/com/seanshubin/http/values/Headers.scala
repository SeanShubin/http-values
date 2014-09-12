package com.seanshubin.http.values

case class Headers(headers:Map[String, String]) {
  def maybeContentType:Option[ContentType] = {
    headers.get("Content-Type").map(ContentType.fromString)
  }

  def setContentType(contentType:ContentType):Map[String, String] = {
    headers + ("Content-Type" -> contentType.toString)
  }

  def effectiveCharset:String = {
    maybeContentType match {
      case Some(ContentType(_, Some(charset))) => charset
      case _ => "ISO-8859-1"
    }
  }
}

object Headers {
  import scala.language.implicitConversions
  implicit def toHeaders(headers:Map[String, String]) = new Headers(headers)
}
