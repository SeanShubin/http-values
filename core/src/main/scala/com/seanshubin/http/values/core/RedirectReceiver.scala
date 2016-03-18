package com.seanshubin.http.values.core

class RedirectReceiver(redirectFunction: String => Option[String]) extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    redirectFunction(request.uri.path) match {
      case Some(newPath) =>
        val statusCode = 301
        val headers = Seq("Location" -> request.uri.copy(path = newPath).toString)
        val body = Seq()
        val response = ResponseValue(statusCode, body, headers)
        response
      case None =>
        throw new RuntimeException(s"Unable to redirect from ${request.uri}")
    }
  }
}
