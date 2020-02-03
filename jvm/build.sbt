ThisBuild / organization := "org.mmadt"
ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1-SNAPSHOT"

lazy val machine = (project in file("machine"))
  .settings(
    name := "machine",
    description := "mm-ADT :: JVM Machine",
    libraryDependencies := List(
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test")
  )


