package rpv

import org.bytedeco.javacv.{FFmpegFrameGrabber, FFmpegFrameRecorder, Java2DFrameConverter}
import zio.{IO, ZIO}
import zio.console.{getStrLn, putStrLn}

import java.awt.{Color, Rectangle}
import java.awt.image.BufferedImage

object ProcessVideo {

  def fillPartOfImage(bi: BufferedImage): IO[Throwable, Unit] = IO.effect {
    val graph = bi.createGraphics();
    graph.setColor(Color.BLACK);
    graph.fill(new Rectangle(100, 100, 100, 100));
    graph.dispose();
  }

  def bufferedImagesEqual(img1:BufferedImage, img2:BufferedImage): Boolean = {
    if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
      for (x <- 0 to img1.getWidth()) {
        for (y <- 0 to img1.getHeight()) {
          if (img1.getRGB(x, y) != img2.getRGB(x, y))
            return false;
        }
      }
    } else {
      return false
    }
    true
  }

  def processImage(index: Int,
                   previousImage: BufferedImage,
                   frameGrabber: FFmpegFrameGrabber,
                   frameRecorder: FFmpegFrameRecorder,
                   imageConverter : Java2DFrameConverter,
                   frameConverter: Java2DFrameConverter
                  ): IO[Throwable, BufferedImage] = for {
    timestamp <- IO.effect {
      frameGrabber.setFrameNumber(index)
      frameGrabber.getTimestamp
    }
    frame <- IO.effect {
      frameGrabber.grab()
    }
    bi <- IO.effect {
      println(frame.timestamp + " : " + frame.keyFrame)
      frameConverter.convert(frame)
    }
    // _ <- fillPartOfImage(bi)

    filledFrame <- IO.effect {
      // converter.convert(bi)
      imageConverter.convert(previousImage)
    }
    _ <- IO.effect {
      frameRecorder.setTimestamp(timestamp)
      if(index % 50 == 0 || index == 297) frameRecorder.record(filledFrame)
    }
  } yield previousImage

  def init(inputPath: String, outputPath: String): IO[Throwable,
    (Java2DFrameConverter, Java2DFrameConverter, FFmpegFrameGrabber, FFmpegFrameRecorder, BufferedImage)] = for{
    imageConverter <- IO.effect {
      new Java2DFrameConverter()
    }
    frameConverter <- IO.effect {
      new Java2DFrameConverter()
    }
    frameGrabber <- IO.effect {
      new FFmpegFrameGrabber(inputPath)
    }
    _ <- IO.effect {
      frameGrabber.start()
    }
    _ <- IO.effect {
      frameGrabber.setFrameNumber(0)
    }
    frame <- IO.effect {
      frameGrabber.grab()
    }
    zeroBi <- IO.effect {
      frameConverter.convert(frame)
    }
    frameRecorder <- IO.effect {
      new FFmpegFrameRecorder(outputPath, zeroBi.getWidth, zeroBi.getHeight)
    }
    _ <- IO.effect {
      frameGrabber.stop()
    }
  } yield(imageConverter, frameConverter, frameGrabber, frameRecorder, zeroBi)

  def processVideo(inputPath: String, outputPath: String): IO[Throwable, Unit] = {
    for {
      (imageConverter, frameConverter, frameGrabber, frameRecorder, zeroBi) <- init(inputPath, outputPath)

      _ <- IO.effect {
        frameGrabber.start()
        frameRecorder.start()
      }
      array =  Array.ofDim[Char](frameGrabber.getLengthInFrames())
        .zipWithIndex

      _ <- IO.effect {
        frameRecorder.setVideoBitrate(frameGrabber.getVideoBitrate)
      }

      _ <- ZIO.foldLeft(array)(zeroBi){(acc, c) =>
        processImage(c._2, acc, frameGrabber, frameRecorder, imageConverter, frameConverter)
      }
      _ <- IO.effect {
        frameGrabber.stop()
        frameRecorder.stop()
      }
    } yield ()
  }

  import rpv.helpers.Implicits.StringImplicits

  val process =
    for {
      _ <- putStrLn("Enter the path of input file [in.mp4]")
      inputPath <- getStrLn
      _ <- putStrLn("Enter the path of output file [out.mp4]")
      outputPath <- getStrLn
      _ <- processVideo(
        inputPath.withDefault("in.mp4"),
        outputPath.withDefault("out.mp4"))
    } yield ()
}