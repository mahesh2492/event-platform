
import cats.effect._
import cats.implicits._
import org.http4s.ember.server.EmberServerBuilder
import routes.{EventRoutes, HealthRoutes}
object Http4sServer extends IOApp.Simple {
  private val healthRoutes = new HealthRoutes[IO]
  private val eventRoutes = new EventRoutes[IO]
  private def allRoutes = healthRoutes.routes <+> eventRoutes.routes
  override def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHttpApp(allRoutes.orNotFound)
      .build
      .use(_ => IO.println("Server Ready! ") *> IO.never)
}
