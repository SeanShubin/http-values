package com.seanshubin.http.values.core

class FallbackReceiver(receiver: Receiver,
                       fallback: Receiver,
                       requestHandler: RequestValue => Unit,
                       responseHandler: (RequestValue, ResponseValue) => Unit,
                       exceptionHandler: RuntimeException => Unit) extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    try {
      requestHandler(request)
      val response = receiver.receive(request)
      responseHandler(request, response)
      response
    } catch {
      case ex: RuntimeException =>
        exceptionHandler(ex)
        fallback.receive(request)
    }
  }
}
