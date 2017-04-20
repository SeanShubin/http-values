package com.seanshubin.http.values.domain

case class Headers(entries: Seq[(String, String)]) {
  private val asMap = entries.toMap

  def get(key: String): Option[String] = asMap.get(key.toLowerCase)

  def contains(key: String): Boolean = asMap.contains(key.toLowerCase)

  def update(key: String, value: String): Headers = {
    Headers(Headers.updateEntry(entries, key -> value))
  }

  def maybeContentType: Option[ContentType] = {
    asMap.get("content-type").map(ContentType.fromString)
  }

  def setContentType(contentType: ContentType): Headers = {
    def isContentType(entry: (String, String)): Boolean = {
      val (key, _) = entry
      key == "content-type"
    }

    val index = entries.indexWhere(isContentType)
    val newContentTypeEntry = "content-type" -> contentType.toString
    if (index == -1) {
      Headers(entries :+ newContentTypeEntry)
    } else {
      Headers(entries.updated(index, newContentTypeEntry))
    }
  }

  def effectiveCharset: String = {
    maybeContentType match {
      case Some(ContentType(_, Some(charset))) => charset
      case _ => "ISO-8859-1"
    }
  }
}

object Headers {
  val Empty = Headers(Seq())

  def fromEntries(entries: Seq[(String, String)]): Headers = {
    val validatedEntries = entries.foldLeft(Seq[(String, String)]())(addEntry)
    Headers(validatedEntries)
  }

  def addEntry(accumulator: Seq[(String, String)], currentEntry: (String, String)): Seq[(String, String)] = {
    val (currentKey, currentValue) = currentEntry
    val newKey = currentKey.toLowerCase
    val index = accumulator.map(_._1).indexOf(newKey)
    if (index == -1) {
      accumulator :+ (newKey, currentValue)
    } else {
      val (_, oldValue) = accumulator(0)
      val newValue = oldValue + "," + currentValue //Wondering why a comma is used?  See http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
      accumulator.updated(index, (newKey, newValue))
    }
  }

  def updateEntry(accumulator: Seq[(String, String)], currentEntry: (String, String)): Seq[(String, String)] = {
    val (currentKey, currentValue) = currentEntry
    val newKey = currentKey.toLowerCase
    val index = accumulator.map(_._1).indexOf(newKey)
    if (index == -1) {
      accumulator :+ (newKey, currentValue)
    } else {
      val newValue = currentValue
      accumulator.updated(index, (newKey, newValue))
    }
  }
}
