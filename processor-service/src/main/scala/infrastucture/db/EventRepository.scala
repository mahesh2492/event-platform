package infrastucture.db

import domain.Event

trait EventRepository[F[_]] {
  def insert(event: Event): F[Int]
}
