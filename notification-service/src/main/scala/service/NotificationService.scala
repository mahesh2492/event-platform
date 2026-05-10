package service

import domain.Event

trait NotificationService[F[_]] {
  def send(event: Event): F[Unit]
}
