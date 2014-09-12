package com.seanshubin.http.values

trait Receiver {
  def receive(request: RequestValue): ResponseValue
}
