package com.seanshubin.http.values.client.google

import com.google.api.client.http._
import com.google.api.client.http.javanet.NetHttpTransport
import com.seanshubin.http.values.core.{IoUtil, RequestValue, ResponseValue, Sender}

import scala.collection.JavaConversions

class HttpSender extends Sender {
  override def send(request: RequestValue): ResponseValue = {
    val httpRequest: HttpRequest = HttpSender.requestMap(request.method.toLowerCase)(request)
    val httpResponse: HttpResponse = httpRequest.execute()
    val statusCode = httpResponse.getStatusCode
    val inputStream = httpResponse.getContent
    val bytes = IoUtil.inputStreamToBytes(inputStream)
    val javaMapHeaders: java.util.Map[String, AnyRef] = httpResponse.getHeaders
    val scalaMapHeaders: Map[String, AnyRef] = JavaConversions.mapAsScalaMap(javaMapHeaders).toSeq.toMap
    val headers: Map[String, String] = scalaMapHeaders.map(headerToEntry)
    val responseValue = ResponseValue(statusCode, bytes, headers)
    responseValue
  }

  def headerToEntry(entry: (String, AnyRef)): (String, String) = {
    val (key, listAsObject) = entry
    val javaList = listAsObject.asInstanceOf[java.util.List[_]]
    val seq: Seq[_] = JavaConversions.asScalaBuffer(javaList)
    val value = seq.mkString(",") //Wondering why a comma is used?  See http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
    (key, value)
  }
}

object HttpSender {
  val httpTransport: HttpTransport = new NetHttpTransport()
  val factory: HttpRequestFactory = httpTransport.createRequestFactory()

  val requestMap: Map[String, RequestValue => HttpRequest] = Map(
    "get" -> ((request: RequestValue) => factory.buildGetRequest(toGenericUrl(request))),
    "delete" -> ((request: RequestValue) => factory.buildDeleteRequest(toGenericUrl(request))),
    "post" -> ((request: RequestValue) => factory.buildPostRequest(toGenericUrl(request), toHttpContent(request))),
    "put" -> ((request: RequestValue) => factory.buildPutRequest(toGenericUrl(request), toHttpContent(request))),
    "patch" -> ((request: RequestValue) => factory.buildPatchRequest(toGenericUrl(request), toHttpContent(request)))
  )

  def toGenericUrl(request: RequestValue): GenericUrl = new GenericUrl(request.uri)

  def toHttpContent(request: RequestValue): HttpContent = {
    import com.seanshubin.http.values.core.Headers.toHeaders
    new ByteArrayContent(request.headers.maybeContentType.get.toString, request.body.toArray)
  }
}
