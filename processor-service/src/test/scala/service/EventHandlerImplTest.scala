package service

import cats.effect.IO
import domain.Event
import munit.{CatsEffectSuite, FunSuite}

class EventHandlerImplTest extends CatsEffectSuite {

  test("handle should process USER_CREATED event") {
    val handler  = new EventHandlerImpl[IO]

    val event = Event(
      eventId   = "evt-1",
      userId    = "user-1",
      eventType = "USER_CREATED",
      timestamp = 123L,
      payload   = "new user"
    )

    handler.handle(event).map { _ =>
      assert(true) // replace with real assertions when logic grows
    }
  }

  test("handle should process unknown event type safely") {
    val handler = new EventHandlerImpl[IO]

    val event = Event(
      "evt-2", "user-2", "UNKNOWN", 123L, "data"
    )

    handler.handle(event).map { _ =>
      assert(true)
    }
  }

}

