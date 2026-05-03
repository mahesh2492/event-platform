package service

import domain.Event
import infrastructure.EventProducer

class EventServiceImpl[F[_]](producer: EventProducer[F]) extends EventService[F] {

  override def process(event: Event): F[Unit] = producer.publish(event)
}
