package rpv.ui.views

import rpv.helpers.Сoncealer
import scalafx.geometry.{Bounds, Pos}
import scalafx.scene.image.ImageView
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{AnchorPane, StackPane}
import scalafx.scene.shape.Rectangle
import scalafx.Includes._
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.image.Image.sfxImage2jfx
import scalafx.scene.paint.Paint

import java.awt.image.BufferedImage
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class FrameView(addConcealer: Сoncealer => Unit) extends AnchorPane{
  val MAX_WIDTH = 1050
  val MAX_HEIGHT = 800

  var initX: Double = 0;
  var initY: Double = 0;
  var currentConcealer: Rectangle = Rectangle(0, 0, 0, 0);

  val imageView = new ImageView {
    preserveRatio = true
    fitWidth = MAX_WIDTH
    fitHeight = MAX_HEIGHT

    onMousePressed = (me) => {
      val (cX, cY) = checkBounds(me.sceneX, me.sceneY)

      initX = cX
      initY = cY

      currentConcealer = new Rectangle {
        id = new Random().nextString(16)
        fill = Paint.valueOf("#FFFFFF")
        stroke = Paint.valueOf("#000000")
        strokeWidth = 1
        translateX = cX
        translateY = cY
        width = 0;
        height = 0;

        toFront()
      }

      children.add(currentConcealer)
    }

    onMouseDragged = (me) => {
      val (cX, cY) = checkBounds(me.sceneX, me.sceneY)

      (cX, cY) match {
        case (sX, sY) if sX >= initX && sY >= initY => {
          currentConcealer.width = sX - initX;
          currentConcealer.height = sY - initY;

        }
        case (sX, sY) if sX < initX && sY >= initY => {
          currentConcealer.width = initX - sX
          currentConcealer.translateX = sX
          currentConcealer.height = sY - initY;
        }
        case (sX, sY) if sX >= initX && sY < initY => {
          currentConcealer.height = initY - sY
          currentConcealer.translateY = sY
          currentConcealer.width = sX - initX;
        }
        case (sX, sY) if sX < initX && sY < initY => {
          currentConcealer.height = initY - sY
          currentConcealer.translateY = sY
          currentConcealer.width = initX - sX
          currentConcealer.translateX = sX
        }
        case _ =>
      }
    }

    onMouseReleased = me => {
      addConcealer(new Сoncealer(
        currentConcealer.id.value,
        currentConcealer.translateX.value,
        currentConcealer.translateY.value,
        currentConcealer.width.value,
        currentConcealer.height.value,
        0, 10
      ))
    }
  }

  def removeConcealer(id: String): Unit = {
    children.removeIf(r => r.id.isNotEmpty.get() && r.id.value.equals(id))
  }

  def updateImage(image: BufferedImage): Unit = {
    imageView.setImage(sfxImage2jfx(
      SwingFXUtils.toFXImage(image, null)
    ))
  }

  def checkBounds(x: Double, y: Double): (Double, Double) = {
    val b: Bounds = imageView.localToScene(imageView.getBoundsInLocal());

    val bWidth = b.minX + b.width
    val bHeight = b.minY + b.height

    val cX = x match {
      case xX if (xX >= b.minX && xX <= bWidth) => xX
      case xX if (xX > bWidth) => bWidth
      case xX if (xX < b.minX) => b.minX
    }
    val cY = y match {
      case yY if (yY >= b.minY && yY <= bHeight) => yY
      case yY if (yY > bHeight) => bHeight
      case yY if (yY < b.minY) => b.minY
    }
    (cX, cY)
  }

  val frameView = new StackPane {
    children = imageView
  }

  StackPane.setAlignment(imageView, Pos.Center);
  children.add(frameView)
  AnchorPane.setAnchors(frameView, 0, 0, 0, 0)
}
