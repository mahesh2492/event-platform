
import cats.effect._
import cats.implicits._
import com.comcast.ip4s.IpLiteralSyntax
import config.AppConfig
import infrastructure.{KafkaEventProducer, KafkaProducerResource}
import org.http4s.ember.server.EmberServerBuilder
import org.slf4j.LoggerFactory
import routes.{EventRoutes, HealthRoutes}
import config.AppConfig
import service.{EventService, EventServiceImpl}
object Http4sServer extends IOApp.Simple {
  override def run: IO[Unit] =
    KafkaProducerResource.create[IO](AppConfig.kafkaConfig).use { kafkaProducer =>
    val eventProducer = new KafkaEventProducer[IO](kafkaProducer, AppConfig.kafkaConfig.topic)
    val eventService = new EventServiceImpl[IO](eventProducer)

     val logger = LoggerFactory.getLogger(getClass)
     val healthRoutes = new HealthRoutes[IO]
     val eventRoutes = new EventRoutes[IO](eventService)

     def allRoutes = healthRoutes.routes <+> eventRoutes.routes

      EmberServerBuilder
        .default[IO]
        .withPort(port"9000")
        .withHttpApp(allRoutes.orNotFound)
        .build
        .use(_ => IO(logger.info("Server Ready! ")) *> IO.never)
  }

}

