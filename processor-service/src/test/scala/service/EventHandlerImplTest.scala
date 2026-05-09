package service

import cats.effect.{IO, Sync}
import domain.Event
import infrastucture.db.EventRepository
import munit.CatsEffectSuite
class EventHandlerImplTest extends CatsEffectSuite {

  test("handle should process USER_CREATED event") {
    val repo = new FakeEventRepository[IO]
    val handler  = new EventHandlerImpl[IO](repo)

    val event = Event(
      eventId   = "evt-1",
      userId    = "user-1",
      eventType = "USER_CREATED",
      timestamp = 123L,
      payload   = "new user"
    )

    handler.handle(event).map { rows =>
      assert(rows > 0) // replace with real assertions when logic grows
    }
  }

  test("handle should process unknown event type safely") {
    val repo = new FakeEventRepository[IO]
    val handler = new EventHandlerImpl[IO](repo)

    val event = Event(
      "evt-2", "user-2", "UNKNOWN", 123L, "data"
    )

    handler.handle(event).map { _ =>
      assert(true)
    }
  }

}

class FakeEventRepository[F[_]: Sync] extends EventRepository[F] {
  var insertedEvents: List[Event] = List.empty

  override def insert(event: Event): F[Int] = {
    Sync[F].delay {
      insertedEvents = insertedEvents :+ event
      1
    }
  }
}

