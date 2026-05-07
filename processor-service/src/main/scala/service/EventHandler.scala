package service

import domain.Event

trait EventHandler[F[_]] {
  def handle(event: Event): F[Int]
}
