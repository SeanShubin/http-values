package com.seanshubin.http.values.server.jetty

import com.seanshubin.http.values.client.apache.{HttpSender => ApacheSender}
import com.seanshubin.http.values.client.google.{HttpSender => GoogleSender}
import com.seanshubin.http.values.domain._
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
      headers = Seq("Content-Type" -> "text/plain; charset=utf-8"))
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
      uriString = s"http://localhost:$port/greeting?foo=bar#fragment",
      method = "get",
      body = Seq(),
      headers = Seq())
    val actualResponse = sender.send(sentRequest).withLowerCaseHeaderKeys
    server.stop()
    val actualRequest = requests(0)
    val requestHeaders = Headers.fromEntries(actualRequest.headers)
    val responseHeaders = Headers.fromEntries(actualResponse.headers)
    val actualUri = actualRequest.uri.toUri
    assert(requests.size === 1)
    assert(actualUri.getScheme === "http")
    assert(actualUri.getHost === "127.0.0.1")
    assert(actualUri.getPath === "/greeting")
    assert(actualUri.getQuery === "foo=bar")
    assert(actualRequest.method === "GET")
    assert(actualRequest.body === Seq())
    assert(requestHeaders.get("Connection") === Some("keep-alive"))
    assert(requestHeaders.get("Host") === Some(s"localhost:$port"))
    assert(requestHeaders.contains("Accept-Encoding"))
    assert(actualResponse.body === "Hello, world!".getBytes("utf-8").toSeq)
    assert(actualResponse.statusCode === 200)
    assert(responseHeaders.get("content-type") === Some("text/plain;charset=utf-8"))
    assert(responseHeaders.get("content-length") === Some("13"))
  }
}
