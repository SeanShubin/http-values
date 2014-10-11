package com.seanshubin.http.values

import scala.annotation.tailrec

object SequenceUtil {
  @tailrec
  def searchForFirstDuplicate(seq: Seq[String]): Option[String] = {
    if (seq.isEmpty) None
    else if (seq.tail.contains(seq.head)) Some(seq.head)
    else searchForFirstDuplicate(seq.tail)
  }
}
