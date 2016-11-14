package com.seanshubin.http.values.domain

abstract class Route(val name: String, val receiver: Receiver) {
  def accept(request: RequestValue): Boolean
}
