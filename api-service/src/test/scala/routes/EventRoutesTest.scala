package routes

import cats.effect.IO
import model.Event
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Status.{BadRequest, Ok}
import org.http4s.circe._

class EventRoutesTest extends CatsEffectSuite {

  test("POST /events accept valid event") {
    val routes = new EventRoutes[IO].routes
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
    val routes = new EventRoutes[IO].routes
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
