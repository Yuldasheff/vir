name := "vir"

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / scalaVersion := "3.0.2"
ThisBuild / useSuperShell := false

val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core"     % "2.6.1",
    "io.circe"      %% "circe-core"    % "0.14.1",
    "io.circe"      %% "circe-parser"  % "0.14.1",
    "org.scalameta" %% "munit"         % "0.7.27" % Test
  ),
  scalacOptions ++= Seq(
    "-Ymacro-annotations",
    "-Wunused:imports",
    "-Werror",
    "-Ypartial-unification"
  ),
  testFrameworks += new TestFramework("munit.Framework")
)

/*lazy val data = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("data"))
  .settings(sharedSettings)*/

lazy val backend = project
  .in(file("backend"))
  .settings(
    sharedSettings,
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % "0.23.4",
      "org.http4s"      %% "http4s-ember-client" % "0.23.4",
      "org.http4s"      %% "http4s-circe"        % "0.23.4",
      "org.http4s"      %% "http4s-dsl"          % "0.23.4",
      "io.circe"        %% "circe-generic"       % "0.14.1" cross CrossVersion.for3Use2_13,
      "org.scalameta"   %% "munit"               % "0.7.29" % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % "1.0.6" % Test,
      "ch.qos.logback"  %  "logback-classic"     % "1.2.6",

    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )

