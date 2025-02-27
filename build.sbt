val scala3Version = "3.5.1"
val javaFXVersion = "23.0.1"

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"


lazy val root = project
  .in(file("."))
  .settings(
      name := "blackjack",
      version := "0.1.0-SNAPSHOT",

      scalaVersion := scala3Version,

      libraryDependencies ++= Seq(
          "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
          "org.scalameta" %% "munit" % "1.0.0" % Test,
          "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      ),
  )
