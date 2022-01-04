package com.seanshubin.http.values.server.jetty

import com.seanshubin.http.values.domain._
import org.eclipse.jetty.server.Server

object EchoServer extends App {
  val receiver = new EchoReceiver()
  val port = 4000
  val server = new Server(port)
  val handler = new ReceiverToJettyHandler(receiver)
  server.setHandler(handler)
  server.start()
  server.join()
}
