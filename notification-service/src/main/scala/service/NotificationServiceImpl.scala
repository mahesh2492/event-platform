package service

import cats.effect.Async
import domain.Event
import cats.implicits._
import org.slf4j.LoggerFactory

class NotificationServiceImpl[F[_]: Async] extends NotificationService[F] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def send(event: Event): F[Unit] = {
    for {
      shouldFail <- Async[F].delay(scala.util.Random.nextBoolean())
      _ <-
        if(shouldFail)
          Async[F].raiseError(
            new RuntimeException("Simulated notification failure")
          )
        else
          Async[F].delay {
            logger.info(
              s"Notification sent for ${event.eventId}"
            )
          }
    } yield ()
  }
}
