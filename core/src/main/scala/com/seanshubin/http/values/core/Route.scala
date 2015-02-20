package com.seanshubin.http.values.core

abstract class Route(val name: String, val receiver: Receiver) {
  def accept(request: RequestValue): Boolean
}
