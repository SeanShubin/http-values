package com.seanshubin.http.values.core

import java.net.URI

case class UriValue(scheme: String, user: String, host: String, port: Int, path: String, query: String, fragment: String) {
  def toUri: URI = new URI(scheme, user, host, port, path, query, fragment)

  override def toString: String = toUri.toString
}

object UriValue {
  def fromString(s: String): UriValue = {
    val uri = new URI(s)
    UriValue(uri.getScheme, uri.getUserInfo, uri.getHost, uri.getPort, uri.getPath, uri.getQuery, uri.getFragment)
  }
}
