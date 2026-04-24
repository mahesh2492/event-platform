package routes

import cats.effect.IO
import munit.CatsEffectSuite
import org.http4s.Status._
import org.http4s._
import org.http4s.implicits._

class HealthRoutesTest extends CatsEffectSuite {

  test("GET /health returns Ok with expected body") {
    val routes = new HealthRoutes[IO].routes
    val httpApp = routes.orNotFound
    val request = Request[IO](Method.GET, uri"/health")

    for {
      response <- httpApp.run(request)
      body <- response.as[String]
    } yield  {
      assertEquals(response.status, Ok)
      assertEquals(body, "All going great!")
    }
  }
}
