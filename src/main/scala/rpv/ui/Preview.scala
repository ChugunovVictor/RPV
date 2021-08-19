package rpv.ui

import org.bytedeco.javacv.{FFmpegFrameGrabber, Java2DFrameConverter}
import zio.IO

import java.awt.image.BufferedImage

case class VideoInfo(framesCount: Int,
                    duration: Long,
                    bitRate: Int){
  def estimateDuration(): String = {
    val pre = duration / 1000000
    val milliseconds = duration % 1000000
    val seconds = pre%60
    val minutes_pre = pre/60
    val minutes = minutes_pre%60
    val hours = minutes_pre/60

    f"$hours%02d:$minutes%02d:$seconds%02d.$milliseconds%06d"
  }
}

class Preview(filePath: String) {
  val frameConverter: Java2DFrameConverter = new Java2DFrameConverter()
  val frameGrabber: FFmpegFrameGrabber = new FFmpegFrameGrabber(filePath)



  def framesCount(): IO[Throwable, VideoInfo] = for {
    _ <- IO.effect { frameGrabber.start() }
    count <- IO.effect { frameGrabber.getLengthInVideoFrames() }
    duration <- IO.effect { frameGrabber.getLengthInTime }
    bitrate <- IO.effect { frameGrabber.getVideoBitrate }
    _ <- IO.effect { frameGrabber.stop() }
  } yield VideoInfo(count, duration, bitrate)

  def showFrame(index: Int): IO[Throwable, BufferedImage] = for {
    frame <- IO.effect {
      frameGrabber.start()
      frameGrabber.setFrameNumber(index)
      frameGrabber.grab()
    }
    bi <- IO.effect {
      frameConverter.convert(frame)
    }
    _ <- IO.effect {
      frameGrabber.stop()
    }
  } yield bi
}