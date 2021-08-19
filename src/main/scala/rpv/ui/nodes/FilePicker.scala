package rpv.ui.nodes

import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.{HBox, Priority}
import scalafx.stage.{FileChooser, Stage}
import zio.IO

class FilePicker(primaryStage: Stage, callback: String => IO[Throwable, Unit]) extends HBox{
  val fileChooser = new FileChooser();
  val path = new TextField{
    hgrow = Priority.Always
  }

  children = Seq(
    path,
    new Button("Choose"){
      onAction = e => {
        path.text = fileChooser.showOpenDialog(primaryStage).getAbsolutePath
        zio.Runtime.default.unsafeRun(callback(path.text.value))
      }
    }
  )
}
