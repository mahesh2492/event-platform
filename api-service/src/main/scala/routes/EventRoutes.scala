package routes

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import domain.Event
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.slf4j.LoggerFactory

class EventRoutes[F[_]: Concurrent] {

  private val logger = LoggerFactory.getLogger(getClass)
  private val dsl = Http4sDsl[F]
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "events" =>
      req.attemptAs[Event].value.flatMap {
        case Right(event) =>
          logger.info(s"Received event request: eventId=${event.eventId}, userId=${event.userId}")
          event.validate match {
            case Left(error) =>
              logger.error(s"Failed to process event $error")
              BadRequest(error)
            case Right(evnt) => Ok(s"Event received $evnt")
          }
        case Left(_) => BadRequest("Invalid event payload")
      }
  }

}

