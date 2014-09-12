package com.seanshubin.http.values

trait Sender {
  def send(request: RequestValue): ResponseValue
}
