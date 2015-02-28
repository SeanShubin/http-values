package com.seanshubin.http.values.core

class PrefixReceiver(prefix: String, forwardTo: Receiver) extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    val rawResponse: ResponseValue = if (request.uriString.startsWith(prefix + "/")) {
      forwardTo.receive(request.copy(uriString = request.uriString.substring(prefix.size)))
    } else if (request.uriString == prefix) {
      forwardTo.receive(request.copy(uriString = "/"))
    } else if (request.uriString == "/favicon.ico") {
      forwardTo.receive(request)
    } else {
      throw new RuntimeException(s"Expected uri to start with '$prefix', got '${request.uriString}'")
    }
    val response = adjustLocation(rawResponse)
    response
  }

  private def adjustLocation(rawResponse: ResponseValue): ResponseValue = {
    val response = rawResponse.headers.get("Location") match {
      case Some(location) =>
        val newLocation = prefix + location
        val newHeaders: Map[String, String] = rawResponse.headers + ("Location" -> newLocation)
        rawResponse.copy(headers = newHeaders)
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
