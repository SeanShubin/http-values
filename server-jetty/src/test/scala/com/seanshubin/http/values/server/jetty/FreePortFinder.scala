package com.seanshubin.http.values.server.jetty

trait FreePortFinder {
  def findFreePort(): Int
}
