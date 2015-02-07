package com.seanshubin.http.values.core

class ClassLoaderGate(name: String, receiver: Receiver, contentTypeByExtension: Map[String, ContentType]) extends Gate(name, receiver) {
  override def accept(request: RequestValue): Boolean = {
    if (request.method == "GET") {
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
  }
}
