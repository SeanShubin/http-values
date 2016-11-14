package com.seanshubin.http.values.domain

trait Sender {
  def send(request: RequestValue): ResponseValue
}
