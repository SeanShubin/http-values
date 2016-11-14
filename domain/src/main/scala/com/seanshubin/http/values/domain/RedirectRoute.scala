package com.seanshubin.http.values.domain

class RedirectRoute(name: String, receiver: Receiver, redirectFunction: String => Option[String]) extends Route(name, receiver) {
  def accept(request: RequestValue): Boolean = {
    val result = redirectFunction(request.uri.path).isDefined
    result
  }
}
