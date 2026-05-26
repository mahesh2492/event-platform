package infrastructure.db

import cats.effect.IO
import domain.{Event, EventType}

trait EventRepository[F[_]] {
  def findAll: F[List[Event]]
  def findById(eventId: String): F[Option[Event]]
  def findByType(eventType: EventType): F[List[Event]]
}
