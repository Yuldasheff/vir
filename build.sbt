name := "vir"

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / scalaVersion := "3.0.2"
ThisBuild / useSuperShell := false

lazy val data = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("data"))
  .settings(sharedSettings)

lazy val backend = project
  .in(file("backend"))
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "0.23.4",
      "org.http4s" %% "http4s-ember-client" % "0.23.4",
      "org.http4s" %% "http4s-circe" % "0.23.4",
      "org.http4s" %% "http4s-dsl" % "0.23.4",
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.6" % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.6"
    )
  )

lazy val frontend = (project in file("frontend"))
  .settings(
    libraryDependencies += "org.scala-js" %% "scalajs-dom" % "1.0.0",
    scalaJSUseMainModuleInitializer :=true
  ).enablePlugins(ScalaJSPlugin)


