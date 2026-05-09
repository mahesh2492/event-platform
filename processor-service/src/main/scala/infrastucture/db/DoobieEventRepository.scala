package infrastucture.db

import cats.effect.Async
import domain.Event
import doobie._
import doobie.implicits._

class DoobieEventRepository[F[_]: Async](xa: Transactor[F]) extends EventRepository[F] {

  def insert(event: Event): F[Int] =
    sql"""
         INSERT INTO events (event_id, user_id, event_type, timestamp, payload)
          VALUES (${event.eventId}, ${event.userId}, ${event.eventType.value}, ${event.timestamp}, ${event.payload});
         """.update.run.transact(xa)
}
