
import cats.effect._
import cats.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.slf4j.LoggerFactory
import routes.{EventRoutes, HealthRoutes}
object Http4sServer extends IOApp.Simple {
  private val logger = LoggerFactory.getLogger(getClass)
  private val healthRoutes = new HealthRoutes[IO]
  private val eventRoutes = new EventRoutes[IO]
  private def allRoutes = healthRoutes.routes <+> eventRoutes.routes
  override def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHttpApp(allRoutes.orNotFound)
      .build
      .use(_ => IO(logger.info("Server Ready! ")) *> IO.never)
}
