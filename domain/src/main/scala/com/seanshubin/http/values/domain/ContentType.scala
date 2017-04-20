package com.seanshubin.http.values.domain

case class ContentType(typeAndSubtype: String, maybeCharset: Option[String]) {
  override def toString: String = maybeCharset match {
    case Some(charset) => s"$typeAndSubtype; charset=$charset"
    case None => typeAndSubtype
  }

  def charset: String = maybeCharset.get
}

object ContentType {
  private val wordPattern = """[\w\-]+"""
  private val maybeSpacesPattern = """\s*"""
  private val contentTypeOnlyPattern = RegexUtil.capture(wordPattern + "/" + wordPattern)
  private val charsetPattern = "charset" + maybeSpacesPattern + "=" + maybeSpacesPattern + RegexUtil.capture(wordPattern)
  private val contentTypePattern =
    contentTypeOnlyPattern + maybeSpacesPattern + RegexUtil.optional(";" + maybeSpacesPattern + charsetPattern)
  private val ContentTypeRegex = contentTypePattern.r

  def fromString(value: String): ContentType = {
    value match {
      case ContentTypeRegex(typeAndSubtype, possiblyNullCharset) =>
        ContentType(typeAndSubtype, Option(possiblyNullCharset))
      case _ =>
        throw new RuntimeException(s"Value '$value' does not match pattern '$ContentTypeRegex' for content type")
    }
  }
}
