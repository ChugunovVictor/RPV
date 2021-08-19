package rpv.tests

import org.bytedeco.javacv.{FFmpegFrameGrabber, Java2DFrameConverter}
import zio.IO
import zio.console.{getStrLn, putStrLn}

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object VideoToImages {

  def writeImage(bi: BufferedImage, path: String, formatName: String): IO[Throwable, Unit] = IO.effect {
    ImageIO.write(bi, formatName, new File(path));
  }

  def convertFrameToImage(index: Int, imagePath: String, formatName: String = "png")
                         (implicit frameGrabber: FFmpegFrameGrabber,
                          converter: Java2DFrameConverter): IO[Throwable, Unit] = for {
    _ <- IO.effect {
      frameGrabber.setFrameNumber(index)
    }
    frame <- IO.effect {
      frameGrabber.grab()
    }
    bi <- IO.effect {
      converter.convert(frame)
    }
    _ <- writeImage(bi, s"$imagePath/$index.$formatName", formatName)
  } yield ()

  def convertVideoToImages(mp4Path: String, imagePath: String): IO[Throwable, Unit] = {
    implicit val converter: Java2DFrameConverter = new Java2DFrameConverter();
    implicit val frameGrabber: FFmpegFrameGrabber = new FFmpegFrameGrabber(mp4Path);

    for {
      _ <- IO.effect {
        frameGrabber.start()
      }
      array = Array.ofDim[Char](frameGrabber.getLengthInFrames())
        .zipWithIndex
        .map { case (_, i) => convertFrameToImage(i, imagePath) }
      _ <- IO.collectAll(array)
      _ <- IO.effect {
        frameGrabber.stop()
      }
    } yield ()
  }

  import rpv.helpers.Implicits.StringImplicits

  val process =
    for {
      _ <- putStrLn("Enter the path of file [in.mp4]")
      mp4Path <- getStrLn
      _ <- putStrLn("Enter the folder path where the images will be saved [out]")
      imagePath <- getStrLn
      _ <- convertVideoToImages(
        mp4Path.withDefault("in.mp4"),
        imagePath.withDefault("out"))
    } yield ()
}
