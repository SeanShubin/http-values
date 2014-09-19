package com.seanshubin.http.jetty_server

import com.seanshubin.http.apache_client.HttpSender
import com.seanshubin.http.values.{Sender, Receiver, ResponseValue, RequestValue}
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
        "User-Agent" -> "Apache-HttpClient/4.3.5 (java 1.5)",
        "Host" -> s"localhost:$port",
        "Accept-Encoding" -> "gzip,deflate"
      ))
    val actualResponse = sender.send(sentRequest)
    server.stop()
    assert(requests.size === 1)
    assert(requests(0) === expectedRequest)
    assert(actualResponse.body === "Hello, world!".getBytes("utf-8").toSeq)
    assert(actualResponse.statusCode === 200)
    assert(actualResponse.headers("Content-Type") === "text/plain; charset=UTF-8")
    assert(actualResponse.headers("Content-Length") === "13")
    assert(actualResponse.headers("Server") === "Jetty(9.2.2.v20140723)")
  }
}
