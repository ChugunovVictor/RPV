package rpv

import zio.App

object Main extends App {
/*
  val program = IO.effectTotal(
    new JFXApp3 {
      override def start(): Unit = {
        stage = new JFXApp3.PrimaryStage {
          title = "Reduce Presentation Video"
          resizable = false
          height = 150
          width = 150
          scene = new Scene() {
            content = new Button("Close")
          }
        }
      }
    }.main(Array())
  )

  def run(args: List[String]) =
    program.exitCode
*/
  def run(args: List[String]) =
    ProcessVideo.process.exitCode
}