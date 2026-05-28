package service

import cats.effect.{IO, Sync}
import domain.{Event, EventType}
import infrastructure.db.EventRepository
import infrastructure.kafka.EventProducer
import munit.{CatsEffectSuite, FunSuite}

class EventServiceImplTest extends CatsEffectSuite {

  test("Service should call producer") {
    val producer = new TestProducer[IO]
    val eventRepository = new TestEventRepository[IO]
    val service = new EventServiceImpl[IO](producer, eventRepository)

    val event = Event("evt-1", "user-1", "Sign UP", 123456L, "user has signed up")
    for {
      _ <- service.process(event)
    } yield assertEquals(producer.published.contains(event), true)
  }

  test("getAllEvents should return all events") {

    val producer = new TestProducer[IO]
    val repository = new TestEventRepository[IO]

    val event1 =
      Event("evt-1", "user-1", "USER_SIGNUP", 123456L, "signup")

    val event2 =
      Event("evt-2", "user-2", "USER_LOGIN", 123457L, "login")

    repository.events = List(event1, event2)

    val service =
      new EventServiceImpl[IO](producer, repository)

    for {
      events <- service.getAllEvents
    } yield {
      assertEquals(events.size, 2)
      assert(events.contains(event1))
      assert(events.contains(event2))
    }
  }

  test("getEventById should return matching event") {

    val producer = new TestProducer[IO]
    val repository = new TestEventRepository[IO]

    val event =
      Event("evt-1", "user-1", "USER_SIGNUP", 123456L, "signup")

    repository.events = List(event)

    val service =
      new EventServiceImpl[IO](producer, repository)

    for {
      result <- service.getEventById("evt-1")
    } yield {
      assertEquals(result, Some(event))
    }
  }

  test("getEventsByType should return filtered events") {

    val producer = new TestProducer[IO]
    val repository = new TestEventRepository[IO]

    val signupEvent =
      Event("evt-1", "user-1", "USER_SIGNUP", 123456L, "signup")

    val loginEvent =
      Event("evt-2", "user-2", "USER_LOGIN", 123457L, "login")

    repository.events = List(signupEvent, loginEvent)

    val service =
      new EventServiceImpl[IO](producer, repository)

    for {
      events <- service.getEventsByType(EventType.UserSignup)
    } yield {
      assertEquals(events.size, 1)
      assertEquals(events.head, signupEvent)
    }
  }
}

class TestProducer[F[_]: Sync] extends EventProducer[F] {

  var published: List[Event] = List.empty
  override def publish(event: Event): F[Unit] =
    Sync[F].delay {
      published = published :+ event
    }
}
class TestEventRepository[F[_]: Sync] extends EventRepository[F] {

  var events: List[Event] = List.empty

  override def findAll: F[List[Event]] =
    Sync[F].pure(events)

  override def findById(eventId: String): F[Option[Event]] =
    Sync[F].pure(
      events.find(_.eventId == eventId)
    )

  override def findByType(eventType: EventType): F[List[Event]] =
    Sync[F].pure(
      events.filter(_.eventType == eventType.value)
    )
}