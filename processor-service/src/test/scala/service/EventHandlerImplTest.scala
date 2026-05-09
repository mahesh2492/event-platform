package service

import cats.effect.{IO, Sync}
import domain.Event
import domain.EventType.UserSignup
import infrastucture.db.EventRepository
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import munit.CatsEffectSuite
class EventHandlerImplTest extends CatsEffectSuite {

  implicit val eventDecoder: Decoder[Event] = deriveDecoder

  test("handle should process USER_CREATED event") {
    val repo = new FakeEventRepository[IO]
    val handler  = new EventHandlerImpl[IO](repo)

    val event = Event(
      eventId   = "evt-1",
      userId    = "user-1",
      eventType = UserSignup,
      timestamp = 123L,
      payload   = "new user"
    )

    handler.handle(event).map { rows =>
      assert(rows > 0) // replace with real assertions when logic grows
    }
  }

  test("should fail decoding unknown event type") {

    val json =
      """
      {
        "eventId":"evt-1",
        "userId":"user-1",
        "eventType":"UNKNOWN",
        "timestamp":123,
        "payload":"data"
      }
    """

    val result = decode[Event](json)

    assert(result.isLeft)
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

