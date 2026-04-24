package routes

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import model.Event
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class EventRoutes[F[_]: Concurrent] {

  private val dsl = Http4sDsl[F]
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "events" =>
      req.attemptAs[Event].value.flatMap {
        case Right(event) => Ok(s"Event received $event")
        case Left(_) => BadRequest("Invalid event payload")
      }
  }

}

