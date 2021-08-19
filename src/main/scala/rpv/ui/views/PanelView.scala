package rpv.ui.views

import rpv.UI.{columnConstraint, stage}
import rpv.helpers.Сoncealer
import rpv.ui.Preview
import rpv.ui.nodes.{FilePicker, TableNode}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{HPos, Insets}
import scalafx.scene.control.{Label, TableView}
import scalafx.scene.layout.{GridPane, Priority}
import zio.IO

import java.awt.image.BufferedImage

class PanelView(showImage: => BufferedImage => Unit,
                updateSlider: Int => Unit,
                removeConcealer: => String => Unit ) extends GridPane {
  var preview: Preview = null;
  var size: Label = new Label("Size:")
  var duration: Label = new Label("Duration:")
  var bitrate: Label = new Label("Bitrate:")

  columnConstraints = Seq(
    columnConstraint(100, align = HPos.Left),
  )

  padding = Insets(5, 5, 5, 5)
  vgap = 5

  def getImage(frameNumber: Int): IO[Throwable,BufferedImage] = {
    preview.showFrame(frameNumber)
  }

  add(new Label("Please specify path for the input video file:"), 0, 0)
  add(new FilePicker(stage, filePath => for {
    _ <- IO.effect {
      preview = new Preview(filePath)
    }
    bi <- preview.showFrame(0)
    _ <- IO.effect {
      showImage(bi)
      size.text = s"Size: ${bi.getWidth}x${bi.getHeight}"
    }
    info <- preview.framesCount()
    _ <- IO.effect {
      updateSlider(info.framesCount - 1)
      duration.text = s"Duration: ${info.estimateDuration()}"
      bitrate.text = s"Bitrate: ${info.bitRate / 1000} kb/s"
    }
  } yield ()
  ), 0, 1)
  add(size, 0, 2)
  add(duration, 0, 3)
  add(bitrate, 0, 4)

  add(new Label("Please specify path for the output video file:"), 0, 6)
  add(new FilePicker(stage, filePath => IO.effect {}), 0, 7)

  add(new Label("Hide-list:"), 0, 9)

  val tableView = new TableNode(removeConcealer)
  def addConcealer(value: Сoncealer): Unit ={
    tableView.items.value.add(value)
  }

  add(tableView, 0, 10)
  GridPane.setVgrow(tableView, Priority.Always)
}
