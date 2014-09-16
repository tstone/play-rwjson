name := "play-rwjson"

scalaVersion := "2.11.2"

version := "0.0.1-pre"

organization := "org.rwjson"

organizationName := "RWJSON"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.0",
  "org.specs2" %% "specs2" % "2.4.2" % "test"
)
