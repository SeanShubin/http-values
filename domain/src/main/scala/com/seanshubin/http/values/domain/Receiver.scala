package com.seanshubin.http.values.domain

trait Receiver {
  def receive(request: RequestValue): ResponseValue
}
