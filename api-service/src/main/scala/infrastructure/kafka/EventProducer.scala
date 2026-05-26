package infrastructure.kafka

import domain.Event

trait EventProducer[F[_]] {
  def publish(event: Event): F[Unit]
}
