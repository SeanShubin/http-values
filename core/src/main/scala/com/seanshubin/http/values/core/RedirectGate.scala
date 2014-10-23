package com.seanshubin.http.values.core

class RedirectGate(name: String, receiver: Receiver, redirectFunction: String => Option[String]) extends Gate(name, receiver) {
  def accept(request: RequestValue): Boolean = {
    redirectFunction(request.uriString).isDefined
  }
}