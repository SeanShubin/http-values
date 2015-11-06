package com.seanshubin.http.values.core

import java.net.URI
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import scala.collection.JavaConversions

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
    val uri = new URI(scheme, userInfo, host, port, path, query, fragment)
    val uriString = uri.toString
    val method = request.getMethod
    val body = IoUtil.inputStreamToBytes(request.getInputStream)
    val headerNames = JavaConversions.enumerationAsScalaIterator(request.getHeaderNames)
    val headerEntries = for {
      headerName <- headerNames
    } yield {
      (headerName, request.getHeader(headerName))
    }
    val headers = Headers.fromEntries(headerEntries.toSeq)
    val value = RequestValue(uriString, method, body, headers.entries)
    value
  }
}
