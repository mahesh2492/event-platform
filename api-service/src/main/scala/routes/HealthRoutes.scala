package routes

import cats.effect.kernel.Concurrent
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class HealthRoutes[F[_]: Concurrent] {

  private val dsl = Http4sDsl[F]
  import dsl._

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "health" => Ok("All going great!")
  }
  
}
