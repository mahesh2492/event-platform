package service

import cats.effect.Async
import domain.Event
import infrastucture.EventHandler
import org.slf4j.LoggerFactory

class EventHandlerImpl[F[_]: Async] extends EventHandler[F] {

  private val logger = LoggerFactory.getLogger(this.getClass)
  override def handle(event: Event): F[Unit] =
    Async[F].delay {
      logger.info(s"Processing event: $event")
    }
}
