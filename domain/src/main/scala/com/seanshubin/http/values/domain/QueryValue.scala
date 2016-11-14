package com.seanshubin.http.values.domain

case class QueryValue(parts: Seq[(String, String)]) {
  override def toString: String = {
    if (parts.isEmpty) null
    else parts.map(partToString).mkString("&")
  }

  private def partToString(part: (String, String)): String = {
    val (key, value) = part
    s"$key=$value"
  }
}

object QueryValue {
  def fromString(s: String): QueryValue = {
    if (s == null) {
      new QueryValue(Seq())
    } else {
      new QueryValue(s.split("&").map(partFromString))
    }
  }

  private def partFromString(s: String): (String, String) = {
    s.split("=").toSeq match {
      case Seq(key, value) => (key, value)
    }
  }
}
