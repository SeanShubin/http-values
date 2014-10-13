package com.seanshubin.http.values.server.jetty

import java.net.ServerSocket

class FreePortFinderImpl extends FreePortFinder {
  override def findFreePort(): Int = {
    val serverSocket: ServerSocket = new ServerSocket(0)
    val port = serverSocket.getLocalPort
    serverSocket.close()
    port
  }
}
