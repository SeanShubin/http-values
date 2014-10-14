package com.seanshubin.http.values.core

class EchoReceiver extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    val statusCode = 200
    val charset: String = "utf-8"
    val body = request.toMultipleLineString.mkString("\n").getBytes(charset)
    val contentType = ContentType("text/plain", Some(charset))
    val headers = Headers(Map()).setContentType(contentType)
    val responseValue = ResponseValue(statusCode, body, headers)
    responseValue
  }
}
