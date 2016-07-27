package com.seanshubin.http.values.core

import java.net.URI

case class UriValue(scheme: String, user: String, host: String, port: Int, path: String, query: QueryValue, fragment: String) {
  def toUri: URI = new URI(scheme, user, host, port, path, query.toString, fragment)

  override def toString: String = {
    val uri = toUri
    uri.getScheme + ":" + uri.getSchemeSpecificPart
  }
}

object UriValue {
  def fromString(s: String): UriValue = {
    val uri = new URI(s)
    val queryValue = QueryValue.fromString(uri.getRawQuery)
    UriValue(uri.getScheme, uri.getRawUserInfo, uri.getHost, uri.getPort, uri.getRawPath, queryValue, uri.getRawFragment)
  }
}
