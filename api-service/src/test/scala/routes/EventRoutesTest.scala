package routes

import cats.effect.{IO, Sync}
import domain.{Event, EventType}
import infrastructure.db.EventRepository
import io.circe.generic.auto._
import io.circe.syntax._
import munit.CatsEffectSuite
import org.http4s.Status.{BadRequest, Ok}
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import service.EventService

class EventRoutesTest extends CatsEffectSuite {

  val testEvent =
    Event(
      "evt-1",
      "user-1",
      "USER_SIGNUP",
      System.currentTimeMillis(),
      "payload"
    )

  test("POST /events accept valid event") {
    val eventService = new TestEventService[IO]
    eventService.received = List(testEvent)

    val routes = new EventRoutes[IO](eventService).routes
    val httpApp = routes.orNotFound
    val event = Event(
      eventId = "1",
      userId = "userId",
      eventType = "signup",
      timestamp = System.currentTimeMillis(),
      payload = "User signed up"
    )

    val request = Request[IO](Method.POST, uri"/events")
      .withEntity(event.asJson)

    for {
      response <- httpApp.run(request)
    } yield {
      assertEquals(response.status, Ok)
    }
  }

  test("POST /events with invalid json returns BadRequest") {
    val eventService = new TestEventService[IO]
    eventService.received = List(testEvent)
    val routes = new EventRoutes[IO](eventService).routes
    val httpApp = routes.orNotFound
    val badJson = """{"invalid": "data"}"""

    val request = Request[IO](Method.POST, uri"/events")
      .withEntity(badJson)

    for {
      response <- httpApp.run(request)
    } yield {
      assertEquals(response.status, BadRequest)
    }
  }

  test("GET /events returns all events") {

    val eventService = new TestEventService[IO]
    eventService.received = List(testEvent)

    val routes = new EventRoutes[IO](eventService).routes
    val httpApp = routes.orNotFound

    val request =
      Request[IO](Method.GET, uri"/events")

    for {
      response <- httpApp.run(request)
      body <- response.as[String]
    } yield {
      assertEquals(response.status, Ok)
      assert(body.contains("evt-1"))
    }
  }

  test("GET /events/type/:type returns filtered events") {

    val eventService = new TestEventService[IO]
    eventService.received = List(testEvent)

    val routes = new EventRoutes[IO](eventService).routes
    val httpApp = routes.orNotFound

    val request =
      Request[IO](Method.GET, uri"/events/type/USER_SIGNUP")

    for {
      response <- httpApp.run(request)
      body <- response.as[String]
    } yield {
      assertEquals(response.status, Ok)
      assert(body.contains("evt-1"))
    }
  }

  test("GET /events/:id returns event") {

    val eventService = new TestEventService[IO]
    eventService.received = List(testEvent)

    val routes = new EventRoutes[IO](eventService).routes
    val httpApp = routes.orNotFound

    val request =
      Request[IO](Method.GET, uri"/events/evt-1")

    for {
      response <- httpApp.run(request)
      body <- response.as[String]
    } yield {
      assertEquals(response.status, Ok)
      assert(body.contains("USER_SIGNUP"))
    }
  }

  test("GET /events/type/INVALID returns bad request") {

    val eventService = new TestEventService[IO]

    val routes = new EventRoutes[IO](eventService).routes
    val httpApp = routes.orNotFound

    val request =
      Request[IO](Method.GET, uri"/events/type/INVALID")

    for {
      response <- httpApp.run(request)
    } yield {
      assertEquals(response.status, BadRequest)
    }
  }


}

class TestEventService[F[_]: Sync] extends EventService[F] {
  var received: List[Event] = List.empty

  def process(event: Event): F[Unit] =
    Sync[F].delay { received = received :+ event }

  override def getAllEvents: F[List[Event]] = Sync[F].pure(received)

  override def getEventById(eventId: String): F[Option[Event]] = Sync[F].pure(received.find(_.eventId == eventId))

  override def getEventsByType(eventType: EventType): F[List[Event]] = Sync[F].pure(received.filter(_.eventType == eventType.value))
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
