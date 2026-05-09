package service

import cats.effect.Async
import domain.Event
import infrastucture.db.EventRepository
import org.slf4j.LoggerFactory
import cats.syntax.all._

class EventHandlerImpl[F[_]: Async](repo: EventRepository[F]) extends EventHandler[F] {

  private val logger = LoggerFactory.getLogger(this.getClass)
  override def handle(event: Event): F[Int] =
    for {
      _   <- handleByType(event)
      res <- repo.insert(event)
    } yield res

  private def handleByType(event: Event): F[Unit] = {
    event.eventType match {
      case "USER_SIGNUP" => Async[F].delay {
        logger.info(s"Starting welcome flow for ${event.userId}")
      }
      case "PURCHASE" => Async[F].delay {
        logger.info(s"High value purchase for ${event.userId}")
      }
      case "PAYMENT_FAILED" => Async[F].delay {
          logger.info(s"Payment has been failed for ${event.userId}")
        }

      case "USER_LOGIN" => Async[F].delay {
          logger.info(s"User ${event.userId} has been logged in.")
        }

      case _ =>
        Async[F].delay {
          logger.info(s"Unhandled event type: ${event.eventType}")
        }
    }
  }
}
