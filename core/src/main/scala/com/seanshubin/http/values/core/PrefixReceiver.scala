package com.seanshubin.http.values.core

class PrefixReceiver(prefix: String, forwardTo: Receiver) extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    val path = request.uri.path
    val rawResponse: ResponseValue = if (path.startsWith(prefix + "/")) {
      forwardTo.receive(request.copy(uri = request.uri.copy(path = path.substring(prefix.length))))
    } else if (path == prefix) {
      forwardTo.receive(request.copy(uri = request.uri.copy(path = "/")))
    } else if (path == "/favicon.ico") {
      forwardTo.receive(request)
    } else {
      throw new RuntimeException(s"Expected uri to start with '$prefix', got '$path'")
    }
    val response = adjustLocation(rawResponse)
    response
  }

  private def adjustLocation(rawResponse: ResponseValue): ResponseValue = {
    val headers = Headers.fromEntries(rawResponse.headers)
    val response = headers.get("Location") match {
      case Some(location) =>
        val locationUri = UriValue.fromString(location)
        val newLocationUri = locationUri.copy(path = prefix + locationUri.path)
        val newHeaders: Headers = headers.update("Location", newLocationUri.toString)
        rawResponse.copy(headers = newHeaders.entries)
      case None => rawResponse
    }
    response
  }
}

object PrefixReceiver {
  def apply(maybePrefix: Option[String], forwardTo: Receiver): Receiver = {
    maybePrefix match {
      case Some(prefix) => new PrefixReceiver(prefix, forwardTo)
      case None => forwardTo
    }
  }
}
