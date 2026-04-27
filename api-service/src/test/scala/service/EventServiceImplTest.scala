package service

import cats.effect.{IO, Sync}
import domain.Event
import infrastructure.EventProducer
import munit.{CatsEffectSuite, FunSuite}

class EventServiceImplTest extends CatsEffectSuite {

  test("Service should call producer") {
    val producer = new TestProducer[IO]
    val service = new EventServiceImpl[IO](producer)

    val event = Event("evt-1", "user-1", "Sign UP", 123456L, "user has signed up")
    for {
      _ <- service.process(event)
    } yield assertEquals(producer.published.contains(event), true)
  }

}

class TestProducer[F[_]: Sync] extends EventProducer[F] {

  var published: List[Event] = List.empty
  override def publish(event: Event): F[Unit] =
    Sync[F].delay {
      published = published :+ event
    }
}