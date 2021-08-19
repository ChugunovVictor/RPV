name := "RPV"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.bytedeco" % "javacpp" % "1.4.1",
  "org.bytedeco" % "javacv" % "1.4.1",
  "org.bytedeco.javacpp-presets" % "opencv-platform" % "3.4.1-1.4.1",
  "org.bytedeco.javacpp-presets" % "ffmpeg-platform" % "3.4.2-1.4.1",

  "org.scalafx" % "scalafx_2.13" % "16.0.0-R24",

  "dev.zio" %% "zio" % "1.0.10"
)