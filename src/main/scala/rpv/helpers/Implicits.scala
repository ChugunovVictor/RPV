package rpv.helpers

object Implicits {
  implicit class StringImplicits(in: String) {
    def withDefault(default: String): String = in match {
      case "" => default
      case _ => in
    }
  }
}
