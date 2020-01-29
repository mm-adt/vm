ThisBuild / organization := "org.mmadt"
ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1-SNAPSHOT"

lazy val core = (project in file("."))
  .settings(
    name := "mmadt",
    description := "mm-ADT :: JVM VM"
  )

lazy val machine = (project in file("machine")).dependsOn(core)
  .settings(
    name := "machine",
    description := "mm-ADT :: Machine"
  )

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"