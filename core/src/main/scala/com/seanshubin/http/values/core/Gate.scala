package com.seanshubin.http.values.core

abstract class Gate(val name: String, val receiver: Receiver) {
  def accept(request: RequestValue): Boolean
}
