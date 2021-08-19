package rpv.tests

import org.bytedeco.javacv.{FFmpegFrameRecorder, Java2DFrameConverter}
import zio.IO
import zio.console.{getStrLn, putStrLn}

import java.awt.image.BufferedImage
import java.awt.{Color, Rectangle}
import java.io.File
import javax.imageio.ImageIO

object ImagesToVideo {

  def readImage(path: String): IO[Throwable, BufferedImage] = IO.effect {
    ImageIO.read(new File(path));
  }

  def fillPartOfImage(bi: BufferedImage): IO[Throwable, Unit] = IO.effect {
    val graph = bi.createGraphics();
    graph.setColor(Color.BLACK);
    graph.fill(new Rectangle(100, 100, 100, 100));
    graph.dispose();
  }

  def numberFromFileName(name: String): Int = name.substring(0, name.size - 4).toInt

  def convertImageToFrame(imagePath: String)
                         (implicit frameRecorder: FFmpegFrameRecorder,
                          converter: Java2DFrameConverter): IO[Throwable, Unit] = for {
    bi <- readImage(imagePath)
    _ <- fillPartOfImage(bi)
    frame <- IO.effect {
      converter.convert(bi)
    }
    _ <- IO.effect {
      frameRecorder.record(frame)
    }
  } yield ()

  def convertImagesToVideo(imagePath: String, mp4Path: String): IO[Throwable, Unit] = {

    val directory: File = new File(imagePath)

    val array = directory.list()
      .filterNot(_.startsWith("."))
      .sortWith { (a, b) => numberFromFileName(a).compare(numberFromFileName(b)) < 0 }

    for {
      zeroBi <- readImage(s"${directory.getAbsolutePath}/${array(0)}")
      converter <- IO.effect {
        new Java2DFrameConverter()
      }
      frameRecorder <- IO.effect {
        new FFmpegFrameRecorder(mp4Path, zeroBi.getWidth, zeroBi.getHeight)
      }
      _ <- IO.effect {
        frameRecorder.start()
      }
      mapped = array.map { case i => convertImageToFrame(s"${directory.getAbsolutePath}/$i")(frameRecorder, converter) }
      _ <- IO.collectAll(mapped)
      _ <- IO.effect {
        frameRecorder.stop()
      }
    } yield ()
  }

  import rpv.helpers.Implicits.StringImplicits

  val process =
    for {
      _ <- putStrLn("Enter the folder path where the images are [in]")
      imagePath <- getStrLn
      _ <- putStrLn("Enter the path where file will be saved [out.mp4]")
      mp4Path <- getStrLn
      _ <- convertImagesToVideo(
        imagePath.withDefault("in"),
        mp4Path.withDefault("out.mp4")
      )
    } yield ()
}
