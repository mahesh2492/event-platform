package infrastucture

import domain.Event

trait EventHandler[F[_]] {
  def handle(event: Event): F[Unit]
}
