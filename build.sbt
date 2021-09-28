val scala3Version = "3.0.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-simple",
    version := "0.1.0",
    scalaVersion := scala3Version
  )
  val Http4sVersion = "0.23.3"
    libraryDependencies ++= Seq(

      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "org.typelevel" %% "cats-effect" % "3.2.9"


    )

