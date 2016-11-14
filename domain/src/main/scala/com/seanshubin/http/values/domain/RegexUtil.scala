package com.seanshubin.http.values.domain

object RegexUtil {
  def optional(pattern: String): String = {
    nonCapture(pattern) + "?"
  }

  def capture(pattern: String): String = {
    s"($pattern)"
  }

  def nonCapture(pattern: String): String = {
    s"(?:$pattern)"
  }
}
