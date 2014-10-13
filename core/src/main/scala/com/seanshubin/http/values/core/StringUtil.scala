package com.seanshubin.http.values.core

object StringUtil {
  val hexGroupSize = 16

  def textToMultipleLineString(caption: String, text: String): Seq[String] = {
    val lines = split(text)
    val header = s"$caption: ${lines.size} lines"
    val quotedLines = lines.map(doubleQuote).map("  " + _)
    val multipleLineString = header +: quotedLines
    multipleLineString
  }

  def bytesToMultipleLineString(caption: String, bytes: Seq[Byte]): Seq[String] = {
    val header = s"$caption: ${bytes.size} bytes"
    val bytesLines: Seq[String] = bytes.grouped(hexGroupSize).map(bytesToString).toSeq.map("  " + _)
    val multipleLineString = header +: bytesLines
    multipleLineString
  }

  def bytesToString(bytes: Seq[Byte]): String = {
    val hexBytes = leftJustify(bytesToHexString(bytes), hexGroupSize * 3)
    val displayBytes = bytesToDisplayString(bytes)
    hexBytes + " " + displayBytes
  }

  def bytesToHexString(bytes: Seq[Byte]): String = {
    bytes.map(byteToHexString).mkString(" ")
  }

  def bytesToDisplayString(bytes: Seq[Byte]): String = {
    bytes.map(byteToDisplayString).mkString
  }

  def byteToHexString(b: Byte): String = f"$b%02x"

  def byteToDisplayString(b: Byte): String = {
    if (b >= 0x20 && b <= 0x7F) b.toChar.toString
    else "."
  }

  def getExtension(name: String): Option[String] = {
    val lastDot = name.lastIndexOf('.')
    val maybeExtension =
      if (name.lastIndexOf('.') == -1) None
      else Some(name.substring(lastDot))
    maybeExtension
  }

  def escape(target: String): String = {
    target.flatMap {
      case '\n' => "\\n"
      case '\b' => "\\b"
      case '\t' => "\\t"
      case '\f' => "\\f"
      case '\r' => "\\r"
      case '\"' => "\\\""
      case '\'' => "\\\'"
      case '\\' => "\\\\"
      case x => x.toString
    }
  }

  def split(target: String): Seq[String] = target.split( """\r\n|\r|\n""")

  def doubleQuote(target: String): String = "\"" + escape(target) + "\""

  def rightJustify(target: String, width: Int): String = {
    val formatString = String.format("%%%ds", width.asInstanceOf[Integer])
    val justified = String.format(formatString, target)
    justified
  }

  def leftJustify(target: String, width: Int): String = {
    val formatString = String.format("%%-%ds", width.asInstanceOf[Integer])
    val justified = String.format(formatString, target)
    justified
  }
}
