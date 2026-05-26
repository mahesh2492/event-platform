package routes

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import domain.{Event, EventType}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.slf4j.LoggerFactory
import service.EventService

class EventRoutes[F[_]: Concurrent](eventService: EventService[F]) {

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

            case Right(evnt) =>
              for {
                _ <- eventService.process(event)
                res <- Ok(s"Event  $evnt has been received for processing")
              } yield res
          }
        case Left(_) => BadRequest("Invalid event payload")
      }

    case GET -> Root / "events" => eventService.getAllEvents.flatMap(Ok(_))

    case GET -> Root / "events" / eventId =>
      eventService.getEventById(eventId).flatMap {
      case Some(event) => Ok(event)
      case None => NotFound(s"Event $eventId not found")
    }

    case GET -> Root / "events" / "type" / eventType =>
      EventType.fromString(eventType) match {
        case Left(error) => BadRequest(error)
        case Right(eventType) =>
          eventService.getEventsByType(eventType).flatMap {
            case Nil => NotFound(s"Event $eventType not found")
            case events: List[Event] => Ok(events)
          }
        }
      }
  }


