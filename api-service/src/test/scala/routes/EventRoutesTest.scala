package routes

import cats.effect.{IO, Sync}
import domain.Event
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Status.{BadRequest, Ok}
import org.http4s.circe._
import service.{EventService, EventServiceImpl}

class EventRoutesTest extends CatsEffectSuite {

  test("POST /events accept valid event") {
    val eventService = new TestEventService[IO]
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
}

class TestEventService[F[_]: Sync] extends EventService[F] {
  var received: List[Event] = List.empty

  def process(event: Event): F[Unit] =
    Sync[F].delay { received = received :+ event }
}
