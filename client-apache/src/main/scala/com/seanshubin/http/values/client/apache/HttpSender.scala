package com.seanshubin.http.values.client.apache

import com.seanshubin.http.values.core._
import org.apache.http.Header
import org.apache.http.client.methods._
import org.apache.http.impl.client.HttpClients

class HttpSender extends Sender {
  override def send(request: RequestValue): ResponseValue = {
    val httpUriRequest: HttpUriRequest = HttpSender.requestMap(request.method)(request)
    val httpClient = HttpClients.createDefault()
    val httpResponse = httpClient.execute(httpUriRequest)
    val statusCode = httpResponse.getStatusLine.getStatusCode
    val inputStream = httpResponse.getEntity.getContent
    val bytes = IoUtil.inputStreamToBytes(inputStream)
    val headerEntries = httpResponse.getAllHeaders.map(headerToEntry)
    val headers = Headers.fromEntries(headerEntries)
    val responseValue = ResponseValue(statusCode, bytes, headers.entries)
    responseValue
  }

  def headerToEntry(header: Header): (String, String) = {
    (header.getName, header.getValue)
  }
}

object HttpSender {
  val requestMap: Map[String, RequestValue => HttpUriRequest] = Map(
    "get" -> ((request: RequestValue) => new HttpGet(request.uri)),
    "delete" -> ((request: RequestValue) => new HttpDelete(request.uri)),
    "post" -> ((request: RequestValue) => withEntity(new HttpPost(request.uri), request)),
    "put" -> ((request: RequestValue) => withEntity(new HttpPut(request.uri), request)),
    "patch" -> ((request: RequestValue) => withEntity(new HttpPatch(request.uri), request))
  )

  def withEntity(request: HttpEntityEnclosingRequestBase, requestValue: RequestValue): HttpUriRequest = {
    val repeatableEntity = new RepeatableEntity(requestValue.body)
    request.setEntity(repeatableEntity)
    request
  }
}