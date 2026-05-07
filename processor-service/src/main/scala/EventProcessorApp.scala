import cats.effect.{IO, IOApp}
import config.{AppConfig, FlywayMigration}
import infrastucture.db.{Database, EventRepository}
import infrastucture.kafka.KafkaEventConsumer
import org.slf4j.{Logger, LoggerFactory}
import service.{EventHandlerImpl, EventProcessor}

object EventProcessorApp extends IOApp.Simple {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  override def run: IO[Unit] = {
    IO {
      // ✅ Force JVM timezone
      System.setProperty("user.timezone", "Asia/Kolkata")
      java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kolkata"))

      logger.info("Running Flyway migration...")
      FlywayMigration.migrate(AppConfig.dbConfig)
    } *>
    Database.transactor[IO](AppConfig.dbConfig).use { xa =>
      val repo = new EventRepository[IO](xa)
      val eventHandler = new EventHandlerImpl[IO](repo)
      val processor: EventProcessor[IO] = new EventProcessor[IO](eventHandler)
      val kafkaConsumer = new KafkaEventConsumer(AppConfig.kafkaConfig, processor)

      kafkaConsumer.stream.compile.drain
    }

  }

}
