package com.seanshubin.http.values.server.jetty

import com.seanshubin.http.values.client.apache.{HttpSender => ApacheSender}
import com.seanshubin.http.values.client.google.{HttpSender => GoogleSender}
import com.seanshubin.http.values.core.{Receiver, RequestValue, ResponseValue, Sender}
import org.eclipse.jetty.server.Server
import org.scalatest.FunSuite

class HttpTest extends FunSuite {
  test("http request apache") {
    testSender(new ApacheSender)
  }

  test("http request google") {
    testSender(new GoogleSender)
  }

  def testSender(sender: Sender) {
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
    val actualResponse = sender.send(sentRequest).withLowerCaseHeaderKeys
    println(actualResponse)
    server.stop()
    val actualRequest = requests(0)
    assert(requests.size === 1)
    assert(actualRequest.uriString === "/greeting")
    assert(actualRequest.method === "GET")
    assert(actualRequest.body === Seq())
    assert(actualRequest.headers("Connection") === "keep-alive")
    assert(actualRequest.headers("Host") === s"localhost:$port")
    assert(actualRequest.headers.contains("Accept-Encoding"))
    assert(actualResponse.body === "Hello, world!".getBytes("utf-8").toSeq)
    assert(actualResponse.statusCode === 200)
    assert(actualResponse.headers("content-type") === "text/plain; charset=UTF-8")
    assert(actualResponse.headers("content-length") === "13")
  }
}
