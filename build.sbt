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
        // gui
        "org.scala-lang.modules" %% "scala-swing" % "3.0.0",

        // scala test
        "org.scalameta" %% "munit" % "1.0.0" % Test,
        "org.scalatest" %% "scalatest" % "3.2.18" % Test,
        "org.scalactic" %% "scalactic" % "3.2.18",
        "org.mockito" % "mockito-core" % "5.14.2",

        // dependency injection
        "net.codingwell" %% "scala-guice" % "7.0.0",
        "com.google.inject" % "guice" % "7.0.0",

        // file io
        "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
        "com.typesafe.play" %% "play-json" % "2.10.5",
      ),
  )
