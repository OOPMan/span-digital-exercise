name := "span-digital-exercise"

organization := "com.github.oopman"

version := "0.1"

scalaVersion := "2.12.6"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
  "io.getquill" %% "quill" % "2.5.4",
  "com.h2database" % "h2" % "1.4.197",
  "mysql" % "mysql-connector-java" % "5.1.38",
  "org.postgresql" % "postgresql" % "9.4.1208",
  "org.xerial" % "sqlite-jdbc" % "3.18.0",
  "com.microsoft.sqlserver" % "mssql-jdbc" % "6.1.7.jre8-preview",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.github.scopt" %% "scopt" % "3.7.0"
)

assemblyMergeStrategy in assembly := {
  case "META-INF/MANIFEST.MF" => MergeStrategy.discard
  case x => MergeStrategy.first
}
