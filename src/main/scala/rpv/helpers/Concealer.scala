package rpv.helpers

import scalafx.beans.property.{ObjectProperty, StringProperty}

class Сoncealer(id_ : String, x_ : Double, y_ : Double,
                width_ : Double, height_ : Double,
                from_ : Int, to_ : Int) {
  val id = new StringProperty(this, "id", id_)
  val x = new ObjectProperty(this, "x", x_)
  val y = new ObjectProperty(this, "y", y_)
  val width = new ObjectProperty(this, "width", width_)
  val height = new ObjectProperty(this, "height", height_)
  val from = new ObjectProperty(this, "from", from_)
  val to = new ObjectProperty(this, "to", to_)
  val action = new ObjectProperty(this, "action", {})

  override def toString: String =
    s"""{
       |    id: $id_,
       |    x: $x_,
       |    y: $y_,
       |    width: $width_,
       |    height: $height_,
       |    from: $from_,
       |    to: $to_
       |}""".stripMargin
}