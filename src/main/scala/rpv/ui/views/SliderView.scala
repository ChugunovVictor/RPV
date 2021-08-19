package rpv.ui.views

import scalafx.Includes._
import scalafx.scene.control.Slider
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority}
import zio.IO

import java.awt.image.BufferedImage

class SliderView(showImage: => BufferedImage => Unit, getImage: Int => IO[Throwable, BufferedImage]) extends GridPane {
  val slider = new Slider() {
    showTickMarks = true
    showTickLabels = true
    handleEvent(MouseEvent.Any) { (me: MouseEvent) =>
      me.eventType match {
        case MouseEvent.MouseReleased => {
          zio.Runtime.default.unsafeRun(for {
            bi <- getImage(value.intValue())
            _ <- IO.effect {
              showImage(bi)
            }
          } yield ())
        }
        case _ =>
      }
    }
  }

  def updateSlider(value: Int): Unit = {
    slider.min = 0
    slider.max = value
    slider.value = 0
  }

  columnConstraints = Seq(
    new ColumnConstraints {
      hgrow = Priority.Always
      percentWidth = 100.0
    },
  )
  add(slider, 0, 0, 3, 1)
}
