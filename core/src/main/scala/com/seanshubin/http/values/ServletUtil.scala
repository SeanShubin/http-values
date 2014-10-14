package com.seanshubin.http.values

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
    val uriString = request.getRequestURI
    val method = request.getMethod
    val body = IoUtil.inputStreamToBytes(request.getInputStream)
    val headerNames = JavaConversions.enumerationAsScalaIterator(request.getHeaderNames)
    val headerEntries = for {
      headerName <- headerNames
    } yield {
      (headerName, request.getHeader(headerName))
    }
    val headers = headerEntries.toMap
    val value = RequestValue(uriString, method, body, headers)
    value
  }
}
