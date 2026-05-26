package service

import domain.{Event, EventType}
import infrastructure.db.EventRepository
import infrastructure.kafka.EventProducer

class EventServiceImpl[F[_]](producer: EventProducer[F], eventRepository: EventRepository[F]) extends EventService[F] {
  override def process(event: Event): F[Unit] = producer.publish(event)

  override def getAllEvents: F[List[Event]] = eventRepository.findAll

  override def getEventById(eventId: String): F[Option[Event]] = eventRepository.findById(eventId)

  override def getEventsByType(eventType: EventType): F[List[Event]] = eventRepository.findByType(eventType)
}
