ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.13.18"

ThisBuild / organization := "com.example"


// -------------------
// Common settings
// -------------------
lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked"
  )
)

// -------------------
// Shared module
// -------------------
lazy val shared = (project in file("shared"))
  .settings(commonSettings)
  .settings(
    name := "shared",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.10.0"
    )
  )

// -------------------
// API Service
// -------------------
lazy val apiService = (project in file("api-service"))
  .dependsOn(shared)
  .settings(commonSettings)
  .settings(
    name := "api-service",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.4",
      "org.http4s" %% "http4s-ember-server" % "0.23.26",
      "org.http4s" %% "http4s-dsl" % "0.23.26",
      "org.http4s" %% "http4s-circe" % "0.23.26",
      "io.circe" %% "circe-generic" % "0.14.7"
    )
  )

// -------------------
// Processor Service
// -------------------
lazy val processorService = (project in file("processor-service"))
  .dependsOn(shared)
  .settings(commonSettings)
  .settings(
    name := "processor-service",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.4"
    )
  )

lazy val root = (project in file("."))
  .aggregate(shared, apiService, processorService)
  .settings(
    name := "event-platform",
    publish / skip := true
  )
