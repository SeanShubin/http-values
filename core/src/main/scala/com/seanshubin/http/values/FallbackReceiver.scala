package com.seanshubin.http.values

class FallbackReceiver(receiver:Receiver,
                       fallback:Receiver,
                       exceptionHandler:RuntimeException => Unit) extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    try {
       receiver.receive(request)
    } catch {
      case ex:RuntimeException =>
        exceptionHandler(ex)
        fallback.receive(request)
    }
  }
}
