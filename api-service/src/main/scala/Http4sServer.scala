
import cats._
import cats.implicits
import cats.effect._
import org.http4s._
import org.http4s.dsl._
import org.http4s.ember.server.EmberServerBuilder

object Http4sServer extends IOApp.Simple {

  def healthEndpoint[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "health" => Ok("All going great!")
    }
  }

  override def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHttpApp(healthEndpoint[IO].orNotFound)
      .build
      .use(_ => IO.println("Server Ready! ") *> IO.never)
}
