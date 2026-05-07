package service

import cats.effect.Async
import domain.Event
import infrastucture.db.EventRepository
import org.slf4j.LoggerFactory

class EventHandlerImpl[F[_]: Async](repo: EventRepository[F]) extends EventHandler[F] {

  private val logger = LoggerFactory.getLogger(this.getClass)
  override def handle(event: Event): F[Int] = repo.insert(event)
}
