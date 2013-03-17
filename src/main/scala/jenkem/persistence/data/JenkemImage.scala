package jenkem.persistence.data

class JenkemImage(val info: ImageInfo, val html: ImageHtml, val css: ImageCss, val irc: ImageIrc) {
  sealed class Part(val c: Class[_ <: ImagePartTrait])
  case object INFO extends Part(classOf[ImageInfo])
  case object HTML extends Part(classOf[ImageHtml])
  case object CSS extends Part(classOf[ImageCss])
  case object IRC extends Part(classOf[ImageIrc])
  val values = List(INFO, HTML, CSS, IRC)
}
