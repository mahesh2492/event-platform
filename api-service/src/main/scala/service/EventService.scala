package service

import domain.{Event, EventType}

trait EventService[F[_]] {
  def process(event: Event): F[Unit]
  def getAllEvents: F[List[Event]]

  def getEventById(eventId: String): F[Option[Event]]

  def getEventsByType(eventType: EventType): F[List[Event]]
}
