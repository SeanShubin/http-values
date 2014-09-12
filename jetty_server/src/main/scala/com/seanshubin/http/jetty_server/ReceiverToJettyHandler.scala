package com.seanshubin.http.jetty_server

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.seanshubin.http.values.{Receiver, ServletUtil}
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

class ReceiverToJettyHandler(server: Receiver) extends AbstractHandler {
  override def handle(target: String,
                      baseRequest: Request,
                      httpServletRequest: HttpServletRequest,
                      httpServletResponse: HttpServletResponse): Unit = {
    val requestValue = ServletUtil.readValue(httpServletRequest)
    val responseValue = server.receive(requestValue)
    baseRequest.setHandled(true)
    ServletUtil.writeValue(responseValue, httpServletResponse)
  }
}
