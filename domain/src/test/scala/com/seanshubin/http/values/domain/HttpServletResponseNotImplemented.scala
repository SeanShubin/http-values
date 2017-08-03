package com.seanshubin.http.values.domain

import java.io.PrintWriter
import java.util
import java.util.Locale
import javax.servlet.ServletOutputStream
import javax.servlet.http.{Cookie, HttpServletResponse}

trait HttpServletResponseNotImplemented extends HttpServletResponse{
  override def sendError(sc: Int, msg: String): Unit = ???

  override def sendError(sc: Int): Unit = ???

  override def getStatus: Int = ???

  override def setHeader(name: String, value: String): Unit = ???

  override def getHeader(name: String): String = ???

  override def addCookie(cookie: Cookie): Unit = ???

  override def setIntHeader(name: String, value: Int): Unit = ???

  override def addDateHeader(name: String, date: Long): Unit = ???

  override def encodeURL(url: String): String = ???

  override def encodeUrl(url: String): String = ???

  override def addHeader(name: String, value: String): Unit = ???

  override def getHeaders(name: String): util.Collection[String] = ???

  override def setDateHeader(name: String, date: Long): Unit = ???

  override def encodeRedirectUrl(url: String): String = ???

  override def encodeRedirectURL(url: String): String = ???

  override def sendRedirect(location: String): Unit = ???

  override def setStatus(sc: Int): Unit = ???

  override def setStatus(sc: Int, sm: String): Unit = ???

  override def getHeaderNames: util.Collection[String] = ???

  override def containsHeader(name: String): Boolean = ???

  override def addIntHeader(name: String, value: Int): Unit = ???

  override def setContentLength(len: Int): Unit = ???

  override def getBufferSize: Int = ???

  override def resetBuffer(): Unit = ???

  override def setContentType(`type`: String): Unit = ???

  override def setBufferSize(size: Int): Unit = ???

  override def isCommitted: Boolean = ???

  override def setCharacterEncoding(charset: String): Unit = ???

  override def setContentLengthLong(len: Long): Unit = ???

  override def getCharacterEncoding: String = ???

  override def flushBuffer(): Unit = ???

  override def getWriter: PrintWriter = ???

  override def getContentType: String = ???

  override def reset(): Unit = ???

  override def getOutputStream: ServletOutputStream = ???

  override def getLocale: Locale = ???

  override def setLocale(loc: Locale): Unit = ???
}
