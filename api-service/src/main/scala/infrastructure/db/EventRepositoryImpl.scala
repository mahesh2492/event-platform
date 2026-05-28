package infrastructure.db

import cats.effect.{Async, IO}
import domain.{Event, EventType}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor

class EventRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends EventRepository[F] {
  override def findAll: F[List[Event]] =
    sql"""
        select
         event_id,
         user_id,
         event_type,
         timestamp,
         payload
        from events;
         """
      .query[Event]
      .to[List]
      .transact(xa)

  override def findById(eventId: String): F[Option[Event]] =
    sql"""
        select
         event_id,
         user_id,
         event_type,
         timestamp,
         payload
         from events where event_id = $eventId;
         """
      .query[Event]
      .option
      .transact(xa)

  override def findByType(eventType: EventType): F[List[Event]] =
    sql"""
        select
         event_id,
         user_id,
         event_type,
         timestamp,
         payload
        from events where event_type = ${eventType.value};
         """
      .query[Event]
      .to[List]
      .transact(xa)
}
