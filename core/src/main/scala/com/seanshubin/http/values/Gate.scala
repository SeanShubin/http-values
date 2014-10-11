package com.seanshubin.http.values

abstract class Gate(val name: String, val receiver: Receiver) {
  def accept(request: RequestValue): Boolean
}
