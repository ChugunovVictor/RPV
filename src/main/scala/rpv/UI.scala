package rpv

import rpv.ui.views.{FrameView, PanelView, SliderView}
import scalafx.application.JFXApp3
import scalafx.geometry.{HPos, Insets}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout._

object UI extends JFXApp3 {

  def columnConstraint(pw: Double, align: HPos = HPos.Center): ColumnConstraints = new ColumnConstraints {
    hgrow = Priority.Always
    percentWidth = pw
    halignment = align
  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Reduce Presentation Video"
      resizable = false
      scene = new Scene() {
        content = new GridPane {
          prefHeight = 850
          prefWidth = 1400
          columnConstraints = Seq(
            columnConstraint(75),
            columnConstraint(25),
          )

          rowConstraints = Seq(
            new RowConstraints {
              vgrow = Priority.Always
              prefHeight = 800
            },
            new RowConstraints {
              vgrow = Priority.Always
              prefHeight = 50
            }
          )

          padding = Insets(5, 5, 5, 5)

          var frameView: FrameView = null
          var panelView: PanelView = null
          val sliderView = new SliderView(
            frameView.updateImage,
            panelView.getImage
          )
          panelView = new PanelView(
            frameView.updateImage,
            sliderView.updateSlider,
            frameView.removeConcealer
          )
          frameView = new FrameView(
            panelView.addConcealer
          )

          add(frameView, 0, 0)
          add(sliderView, 0, 1)
          add(panelView, 1, 0)
          add(new Button("Button"), 1, 1)
        }
      }
    }
  }
}