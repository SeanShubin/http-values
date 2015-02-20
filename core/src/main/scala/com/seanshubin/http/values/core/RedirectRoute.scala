package com.seanshubin.http.values.core

class RedirectRoute(name: String, receiver: Receiver, redirectFunction: String => Option[String]) extends Route(name, receiver) {
  def accept(request: RequestValue): Boolean = {
    redirectFunction(request.uriString).isDefined
  }
}