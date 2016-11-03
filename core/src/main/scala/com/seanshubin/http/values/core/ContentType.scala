package com.seanshubin.http.values.core

case class ContentType(typeAndSubtype: String, maybeCharset: Option[String]) {
  override def toString: String = maybeCharset match {
    case Some(charset) => s"$typeAndSubtype; charset=$charset"
    case None => typeAndSubtype
  }

  def charset: String = maybeCharset.get
}

object ContentType {
  val wordPattern = """[\w\-]+"""
  val maybeSpacesPattern = """\s*"""
  val contentTypeOnlyPattern = RegexUtil.capture(wordPattern + "/" + wordPattern)
  val charsetPattern = "charset" + maybeSpacesPattern + "=" + maybeSpacesPattern + RegexUtil.capture(wordPattern)
  val contentTypePattern =
    contentTypeOnlyPattern + maybeSpacesPattern + RegexUtil.optional(";" + maybeSpacesPattern + charsetPattern)
  val ContentTypeRegex = contentTypePattern.r

  def fromString(value: String): ContentType = {
    value match {
      case ContentTypeRegex(typeAndSubtype, possiblyNullCharset) =>
        ContentType(typeAndSubtype, Option(possiblyNullCharset))
      case _ =>
        throw new RuntimeException(s"Value '$value' does not match pattern '$ContentTypeRegex' for content type")
    }
  }
}
