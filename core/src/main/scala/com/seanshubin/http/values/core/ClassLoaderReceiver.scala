package com.seanshubin.http.values.core

import java.io.{FileInputStream, FileNotFoundException, InputStream}

class ClassLoaderReceiver(classLoader: ClassLoader,
                          prefix: String,
                          contentTypeByExtension: Map[String, ContentType],
                          overridePath: Option[String]) extends Receiver {
  override def receive(request: RequestValue): ResponseValue = {
    val resourceName = prefix + request.uriString
    val inputStream = createInputStreamFor(resourceName)
    if (inputStream == null) {
      throw new RuntimeException(s"Unable to find $resourceName on the class path")
    } else {
      val maybeExtension = StringUtil.getExtension(request.uriString)
      maybeExtension match {
        case Some(extension) =>
          val maybeContentType = contentTypeByExtension.get(extension)
          maybeContentType match {
            case Some(contentType) =>
              val statusCode = 200
              val body = IoUtil.inputStreamToBytes(inputStream)
              val headers = new Headers(Map()).setContentType(contentType)
              val response = ResponseValue(statusCode, body, headers)
              response
            case None =>
              throw new RuntimeException(s"Unable to find content type for extension $extension")
          }
        case None =>
          throw new RuntimeException(s"Unable to find extension for ${request.uriString} (needed to compute content type)")
      }
    }
  }

  private def createInputStreamFor(resourceName: String): InputStream = {
    overridePath match {
      case Some(path) =>
        try {
          val result = new FileInputStream(path + resourceName)
          result
        } catch {
          case ex: FileNotFoundException =>
            val result = classLoader.getResourceAsStream(resourceName)
            result
        }
      case None =>
        val result = classLoader.getResourceAsStream(resourceName)
        result
    }
  }
}
