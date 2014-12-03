package com.seanshubin.http.values.server.jetty

import com.seanshubin.http.values.client.apache.HttpSender
import com.seanshubin.http.values.core.{Receiver, RequestValue, ResponseValue, Sender}
import org.eclipse.jetty.server.Server
import org.scalatest.FunSuite

class HttpTest extends FunSuite {
  test("http request") {
    val sender: Sender = new HttpSender
    var requests: Seq[RequestValue] = Seq()
    val expectedResponse = ResponseValue(
      statusCode = 200,
      body = "Hello, world!".getBytes("utf-8"),
      headers = Map("Content-Type" -> "text/plain; charset=utf-8"))
    val receiver = new Receiver {
      override def receive(request: RequestValue): ResponseValue = {
        requests = requests :+ request
        expectedResponse
      }
    }
    val port = new FreePortFinderImpl().findFreePort()
    val server = new Server(port)
    val handler = new ReceiverToJettyHandler(receiver)
    server.setHandler(handler)
    server.start()
    val sentRequest: RequestValue = RequestValue(
      uriString = s"http://localhost:$port/greeting",
      method = "get",
      body = Seq(),
      headers = Map())
    val expectedRequest: RequestValue = RequestValue(
      s"/greeting",
      "GET",
      Seq(),
      Map("Connection" -> "keep-alive",
        "Host" -> s"localhost:$port",
        "Accept-Encoding" -> "gzip,deflate"
      ))
    val actualResponse = sender.send(sentRequest)
    server.stop()
    val actualRequest = requests(0)
    assert(requests.size === 1)
    assert(actualRequest.uriString === expectedRequest.uriString)
    assert(actualRequest.method === expectedRequest.method)
    assert(actualRequest.body === expectedRequest.body)
    assert(actualRequest.headers("Connection") === expectedRequest.headers("Connection"))
    assert(actualRequest.headers("Host") === expectedRequest.headers("Host"))
    assert(actualRequest.headers("Accept-Encoding") === expectedRequest.headers("Accept-Encoding"))
    assert(actualResponse.body === "Hello, world!".getBytes("utf-8").toSeq)
    assert(actualResponse.statusCode === 200)
    assert(actualResponse.headers("Content-Type") === "text/plain; charset=UTF-8")
    assert(actualResponse.headers("Content-Length") === "13")
  }
}
