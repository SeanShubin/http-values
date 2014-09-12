package com.seanshubin.http.jetty_server

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.seanshubin.http.values.{Receiver, ServletUtil}
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

class ReceiverToJettyHandler(server: Receiver) extends AbstractHandler {
  override def handle(target: String,
                      baseRequest: Request,
                      httpServletRequest: HttpServletRequest,
                      httpServletResponse: HttpServletResponse): Unit = {
    val requestValue = ServletUtil.readValue(httpServletRequest)
    val maybeResponseValue = server.receive(requestValue)
    maybeResponseValue match {
      case Some(responseValue) =>
        baseRequest.setHandled(true)
        ServletUtil.writeValue(responseValue, httpServletResponse)
      case None =>
        baseRequest.setHandled(false)
    }
  }
}
