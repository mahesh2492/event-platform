package api

import cats.effect._
import cats.implicits._
import com.comcast.ip4s.IpLiteralSyntax
import config.AppConfig
import infrastructure.db.{DatabaseConfig, EventRepository, EventRepositoryImpl}
import infrastructure.kafka.{KafkaEventProducer, KafkaProducerResource}
import org.http4s.ember.server.EmberServerBuilder
import org.slf4j.LoggerFactory
import routes.{EventRoutes, HealthRoutes}
import service.EventServiceImpl
object Http4sServer extends IOApp.Simple {
  override def run: IO[Unit] =
    DatabaseConfig.transactor[IO].use { xa =>

      KafkaProducerResource.create[IO](AppConfig.kafkaConfig).use { kafkaProducer =>

        val eventProducer =
          new KafkaEventProducer[IO](
            kafkaProducer,
            AppConfig.kafkaConfig.topic
          )

        val eventRepository =
          new EventRepositoryImpl[IO](xa)

        val eventService =
          new EventServiceImpl[IO](
            eventProducer,
            eventRepository
          )

        val logger = LoggerFactory.getLogger(getClass)

        val healthRoutes = new HealthRoutes[IO]

        val eventRoutes =
          new EventRoutes[IO](eventService)

        def allRoutes =
          healthRoutes.routes <+> eventRoutes.routes

        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"9000")
          .withHttpApp(allRoutes.orNotFound)
          .build
          .use(_ =>
            IO(logger.info("Server Ready! ")) *> IO.never
          )
      }
    }

}

