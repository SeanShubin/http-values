package com.seanshubin.http.values.core

trait Sender {
  def send(request: RequestValue): ResponseValue
}
