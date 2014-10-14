package com.seanshubin.http.values.core

class FallbackReceiver(receiver:Receiver,
                       fallback:Receiver,
                       requestEvent:RequestValue => Unit,
                       responseEvent:(RequestValue, ResponseValue) => Unit,
                       exceptionEvent:RuntimeException => Unit) extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    val response = try {
      requestEvent(request)
      receiver.receive(request)
    } catch {
      case ex:RuntimeException =>
        exceptionEvent(ex)
        fallback.receive(request)
    }
    responseEvent(request, response)
    response
  }
}
