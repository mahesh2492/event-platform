package service

import domain.Event

trait EventService[F[_]] {
  def process(event: Event): F[Unit]
}
