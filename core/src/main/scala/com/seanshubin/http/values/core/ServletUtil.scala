package com.seanshubin.http.values.core

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import scala.collection.JavaConverters._

object ServletUtil {
  def writeValue(value: ResponseValue, response: HttpServletResponse): Unit = {
    val ResponseValue(statusCode, body, headers) = value
    response.setStatus(statusCode)
    for {
      (key, value) <- headers
    } {
      response.setHeader(key, value)
    }
    IoUtil.bytesToOutputStream(body.toArray, response.getOutputStream)
  }

  def readValue(request: HttpServletRequest): RequestValue = {
    val path = request.getRequestURI
    val query = request.getQueryString
    val host = request.getRemoteHost
    val port = request.getRemotePort
    val userInfo = request.getRemoteUser
    val scheme = request.getScheme
    val fragment = null
    val queryValue = QueryValue.fromString(query)
    val uri = new UriValue(scheme, userInfo, host, port, path, queryValue, fragment)
    val method = request.getMethod
    val body = IoUtil.inputStreamToBytes(request.getInputStream)
    val headerNames = request.getHeaderNames.asScala
    val headerEntries = for {
      headerName <- headerNames
    } yield {
      (headerName, request.getHeader(headerName))
    }
    val headers = Headers.fromEntries(headerEntries.toSeq)
    val value = RequestValue(uri, method, body, headers.entries)
    value
  }
}
