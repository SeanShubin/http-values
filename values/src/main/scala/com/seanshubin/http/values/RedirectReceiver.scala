package com.seanshubin.http.values

class RedirectReceiver(redirectFunction: String => Option[String]) extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    redirectFunction(request.uriString) match {
      case Some(newUriString) =>
        val statusCode = 301
        val headers = Map("Location" -> newUriString)
        val body = Seq()
        val response = ResponseValue(statusCode, body, headers)
        response
      case None =>
        throw new RuntimeException(s"Unable to redirect from ${request.uriString}")
    }
  }
}
