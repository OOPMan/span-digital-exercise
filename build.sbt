name := "span-digital-exercise"

organization := "com.github.oopman"

version := "1.0.2"

scalaVersion := "2.12.6"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
  "io.getquill" %% "quill" % "2.5.4",
  "com.h2database" % "h2" % "1.4.197",
  "mysql" % "mysql-connector-java" % "5.1.38",
  "org.postgresql" % "postgresql" % "9.4.1208",
  "org.xerial" % "sqlite-jdbc" % "3.18.0",
  "com.microsoft.sqlserver" % "mssql-jdbc" % "6.1.7.jre8-preview",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.github.scopt" %% "scopt" % "3.7.0",
  "org.slf4j" % "slf4j-nop" % "1.7.25"
)

assemblyMergeStrategy in assembly := {
  case "META-INF/MANIFEST.MF" => MergeStrategy.discard
  case x => MergeStrategy.first
}
