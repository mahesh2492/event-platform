package service

import cats.effect.Async
import cats.syntax.all._
import domain.Event
import domain.EventType._
import infrastucture.db.EventRepository
import org.slf4j.LoggerFactory

class EventHandlerImpl[F[_]: Async](repo: EventRepository[F]) extends EventHandler[F] {

  private val logger = LoggerFactory.getLogger(this.getClass)
  override def handle(event: Event): F[Int] =
    for {
      res <- repo.insert(event)
      _ <- if(res == 1) {
        handleByType(event)
      } else {
        Async[F].delay {
          logger.info(s"Duplicate event ignored: ${event.eventId}")
        }
      }
    } yield res

  private def handleByType(event: Event): F[Unit] = {
    event.eventType match {
      case UserSignup => Async[F].delay {
        logger.info(s"Starting welcome flow for ${event.userId}")
      }
      case Purchase => Async[F].delay {
        logger.info(s"High value purchase for ${event.userId}")
      }
      case PaymentFailed => Async[F].delay {
          logger.info(s"Payment has been failed for ${event.userId}")
        }

      case UserLogin => Async[F].delay {
          logger.info(s"User ${event.userId} has been logged in.")
        }

      case _ =>
        Async[F].delay {
          logger.info(s"Unhandled event type: ${event.eventType}")
        }
    }
  }
}
