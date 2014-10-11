package com.seanshubin.http.jetty_server

trait FreePortFinder {
  def findFreePort(): Int
}
