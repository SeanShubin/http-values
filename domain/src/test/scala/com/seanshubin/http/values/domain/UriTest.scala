package com.seanshubin.http.values.domain

import java.net.URI

import org.scalatest.FunSuite

class UriTest extends FunSuite {
  test("verify actual uri behavior") {
    //see http://tools.ietf.org/html/rfc3986
    val uri: URI = new URI("foo://user@example.com:8042/over/there?name=ferret#nose")
    assert(uri.getScheme === "foo")
    assert(uri.getAuthority === "user@example.com:8042")
    assert(uri.getPath === "/over/there")
    assert(uri.getQuery === "name=ferret")
    assert(uri.getFragment === "nose")
    assert(uri.getHost === "example.com")
    assert(uri.getPort === 8042)
    assert(uri.getSchemeSpecificPart === "//user@example.com:8042/over/there?name=ferret")
    assert(uri.getUserInfo === "user")
    assert(uri.toString === "foo://user@example.com:8042/over/there?name=ferret#nose")
    assert(uri.toASCIIString === "foo://user@example.com:8042/over/there?name=ferret#nose")
  }

  test("forward slashes in query string") {
    //see http://tools.ietf.org/html/rfc3986
    val uri: URI = new URI("foo://user@example.com:8042/over/there?name=/a/b/c#nose")
    assert(uri.getScheme === "foo")
    assert(uri.getAuthority === "user@example.com:8042")
    assert(uri.getPath === "/over/there")
    assert(uri.getQuery === "name=/a/b/c")
    assert(uri.getFragment === "nose")
    assert(uri.getHost === "example.com")
    assert(uri.getPort === 8042)
    assert(uri.getSchemeSpecificPart === "//user@example.com:8042/over/there?name=/a/b/c")
    assert(uri.getUserInfo === "user")
    assert(uri.toString === "foo://user@example.com:8042/over/there?name=/a/b/c#nose")
    assert(uri.toASCIIString === "foo://user@example.com:8042/over/there?name=/a/b/c#nose")
  }

  test("compose uri from parts") {
    val uri: URI = new URI("foo", "user", "example.com", 8042, "/over/there", "name=ferret", "nose")
    assert(uri.getScheme === "foo")
    assert(uri.getAuthority === "user@example.com:8042")
    assert(uri.getPath === "/over/there")
    assert(uri.getQuery === "name=ferret")
    assert(uri.getFragment === "nose")
    assert(uri.getHost === "example.com")
    assert(uri.getPort === 8042)
    assert(uri.getSchemeSpecificPart === "//user@example.com:8042/over/there?name=ferret")
    assert(uri.getUserInfo === "user")
    assert(uri.toString === "foo://user@example.com:8042/over/there?name=ferret#nose")
    assert(uri.toASCIIString === "foo://user@example.com:8042/over/there?name=ferret#nose")
  }

  test("compose uri from path only") {
    val uri: URI = new URI("/over/there")
    assert(uri.getScheme === null)
    assert(uri.getAuthority === null)
    assert(uri.getPath === "/over/there")
    assert(uri.getQuery === null)
    assert(uri.getFragment === null)
    assert(uri.getHost === null)
    assert(uri.getPort === -1)
    assert(uri.getSchemeSpecificPart === "/over/there")
    assert(uri.getUserInfo === null)
    assert(uri.toString === "/over/there")
    assert(uri.toASCIIString === "/over/there")
  }

  test("uri encoded forward slashes") {
    //see http://tools.ietf.org/html/rfc3986
    val uri: URI = new URI("foo://us%2Fer@example.com:8042/aaa/bbb%2Fccc/ddd?na%2Fme=ferret#no%2Fse")
    assert(uri.getScheme === "foo")
    assert(uri.getAuthority === "us/er@example.com:8042")
    assert(uri.getRawAuthority === "us%2Fer@example.com:8042")
    assert(uri.getPath === "/aaa/bbb/ccc/ddd")
    assert(uri.getRawPath === "/aaa/bbb%2Fccc/ddd")
    assert(uri.getQuery === "na/me=ferret")
    assert(uri.getRawQuery === "na%2Fme=ferret")
    assert(uri.getFragment === "no/se")
    assert(uri.getRawFragment === "no%2Fse")
    assert(uri.getHost === "example.com")
    assert(uri.getPort === 8042)
    assert(uri.getSchemeSpecificPart === "//us/er@example.com:8042/aaa/bbb/ccc/ddd?na/me=ferret")
    assert(uri.getRawSchemeSpecificPart === "//us%2Fer@example.com:8042/aaa/bbb%2Fccc/ddd?na%2Fme=ferret")
    assert(uri.getUserInfo === "us/er")
    assert(uri.getRawUserInfo === "us%2Fer")
    assert(uri.toString === "foo://us%2Fer@example.com:8042/aaa/bbb%2Fccc/ddd?na%2Fme=ferret#no%2Fse")
    assert(uri.toASCIIString === "foo://us%2Fer@example.com:8042/aaa/bbb%2Fccc/ddd?na%2Fme=ferret#no%2Fse")
  }
}
