package routes

import cats.effect.kernel.Concurrent
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.slf4j.LoggerFactory

class HealthRoutes[F[_]: Concurrent] {

  val logger = LoggerFactory.getLogger(getClass)
  private val dsl = Http4sDsl[F]
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "health" =>
      logger.info("Incoming request: GET /health")
      Ok("All going great!")
  }

}
