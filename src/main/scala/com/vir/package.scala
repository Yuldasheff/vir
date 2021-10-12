package com

import org.http4s.circe.*
import org.http4s.*

import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.headers.*
import org.http4s.implicits.*
import org.http4s.server.*
import scala.util.Try

import java.time.Year

package object vir extends IOApp{

  type Writer = String

  case class Writers(FirstName: String, LastName: String){
    override def toString: Writer = s"$Name $LastName"
  }
  case class Assignments(id: String, Title: String, writer: Writer, Status: String, WordCount: Int, Headings: Int, Paragraphs: Int, Images: Int, Comments: String)

  case class WriterDetails(firstName: String, lastName: String, genre: String)

  val snjl: Assignments = Assignments(
    "6bcbca1e-efd3-411d-9f7c-14b872444fce",
    "Zack Snyder's Justice League",
    2021,
    List("Henry Cavill", "Gal Godot", "Ezra Miller", "Ben Affleck", "Ray Fisher", "Jason Momoa"),
    "Zack Snyder"
  )

  val assignments: Map[String, Assignments] = Map(snjl.id -> snjl)

  private def findAssignmentById(movieId: UUID) =
    movies.get(movieId.toString)

  private def findAssignmentByWriter(director: String): List[Movie] =
    assignments.values.filter(_.writer == writer).toList


  implicit val yearQueryParamDecoder: QueryParamDecoder[Year] =
    QueryParamDecoder[Int].emap{ yearInt =>
      Try(Year.of(yearInt))
        .toEither
        .leftMap{ e =>
          ParseFailure(e.getMessage, e.getMessage)
        }

    }

  object AssignmentQueryParamMatcher extends QueryParamDecoderMatcher[String]("assignment")
  object YearQueryParamMatcher extends OptionalValidatedQueryParamDecoderMatcher[Year]("year")

  def assignmentRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]

    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "assignment" :? AssignmentQueryParamMatcher(writer: Writer) +& YearQueryParamMatcher(maybeyear) =>
        val assignmentByWriter = findAssignmentByWriter(writer)
        maybeyear match {
          case Some(y) =>
            validateYear.fold(
              _ => BadRequest("The year waas badly formatted"),
              year => {

              val assignmentByWriterAndYear = assignmentByWriter.filter(_.year = year.getValue)
              Ok(assignmentByWriterAndYear.asJson)
        }
            )
          case None => Ok(assignmentByWriter.asJson)
        }
      case GET -> Root / "assignment" / UUIDVar(assignmentId) / "assignments" =>
        findAssignmentById(assignmentId).map(_.writers) match {
          case Some(actors) => Ok(writers.asJson)
          case _ => NotFound("No Assignment with this id")
        }
    }
  }

  object WriterPath{
    def unapply(str: String): Option[Writer] = {
      Try {
        val tokens = str.split(" ")
        Writer(tokens(0), tokens(1))
      }.toOption
    }
  }

  val writerDetailsDB: mutable.Map[Writers, WriterDetails] =
    mutable.Map(Writers("Arisha", "Naz") -> writerDetailsDB("Arisha", "Naz", "languages"))

  def writerRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]

    import dsl._

    HttpRoutes.of[F]{
      case GET -> Root / "writers" / WriterPath(writer) =>
        writerDetailsDB.get(writer) match {
          case Some(wriDetails) => Ok(wriDetails.asJson)
          case _ => NotFound(s"No writer '$writer'")
        }
    }
  }

  def allRoutes[F[_]: Monad]: HttpRoutes[F] =
    assignmentRoutes[F] <+> writerRoutes[F]

  def allRoutesComplete[F[_]: Monad]: HttpApp[F] =
    allRoutes[F].orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    val apis = Routee(
      "/api" -> assignmentRoutes[IO]
    ).orNotFound
  }

  BlazeServerBuilder[IO](runtime.compute)
    .bindHttp(8080, "localhost")
    .withHttpApp(allRoutesComplete)
    .resource
    .use(_ => IO.never)
    .as(ExitCode.success)

}
