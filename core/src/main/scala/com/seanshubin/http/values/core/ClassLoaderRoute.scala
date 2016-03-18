package com.seanshubin.http.values.core

class ClassLoaderRoute(name: String, receiver: Receiver, contentTypeByExtension: Map[String, ContentType]) extends Route(name, receiver) {
  override def accept(request: RequestValue): Boolean = {
    val result = if (request.method == "GET") {
      StringUtil.getExtension(request.uriString) match {
        case Some(extension) =>
          if (contentTypeByExtension.contains(extension)) true
          else false
        case None =>
          false
      }
    } else {
      false
    }
    result
  }
}
