name := "SPAN Digital Exercise"

organization := "com.github.oopman"

version := "0.1"

scalaVersion := "2.12.6"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
  "io.getquill" %% "quill" % "2.5.4",
  "com.h2database" % "h2" % "1.4.197",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.github.scopt" %% "scopt" % "3.7.0"
)
