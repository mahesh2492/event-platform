package processor

import cats.effect.{IO, IOApp}
import config.{AppConfig, FlywayMigration}
import infrastucture.db.{Database, DoobieEventRepository}
import infrastucture.kafka.KafkaEventConsumer
import io.prometheus.client.exporter.HTTPServer
import metrics.Metrics
import org.slf4j.{Logger, LoggerFactory}
import service.{EventHandlerImpl, EventProcessor}

import scala.jdk.CollectionConverters._

object EventProcessorApp extends IOApp.Simple {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  override def run: IO[Unit] = {
    IO {
      // ✅ Force JVM timezone
      System.setProperty("user.timezone", "Asia/Kolkata")
      java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kolkata"))

      logger.info("Running Flyway migration...")
      FlywayMigration.migrate(AppConfig.dbConfig)

      // Start Prometheus metrics server
      logger.info("Starting Prometheus server...")
      new HTTPServer(9095)
      import io.prometheus.client.CollectorRegistry
      logger.info(
        s"Registered metrics: ${CollectorRegistry.defaultRegistry.metricFamilySamples().asScala.size}"
      )
    } *>
    Database.transactor[IO](AppConfig.dbConfig).use { xa =>
      val repo = new DoobieEventRepository[IO](xa)
      val eventHandler = new EventHandlerImpl[IO](repo)
      val processor: EventProcessor[IO] = new EventProcessor[IO](eventHandler)
      val kafkaConsumer = new KafkaEventConsumer(AppConfig.kafkaConfig, processor)
      kafkaConsumer.stream.compile.drain
    }

  }

}
