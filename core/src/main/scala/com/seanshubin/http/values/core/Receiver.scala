package com.seanshubin.http.values.core

trait Receiver {
  def receive(request: RequestValue): ResponseValue
}
