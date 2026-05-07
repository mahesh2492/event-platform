import cats.effect.{IO, IOApp}
import config.{AppConfig, FlywayMigration}
import domain.Event
import fs2.kafka.KafkaConsumer
import infrastucture.db.{Database, EventRepository}
import infrastucture.kafka.KafkaEventConsumer
import service.{EventHandlerImpl, EventProcessor}

object EventConsumerApp extends IOApp.Simple {
  override def run: IO[Unit] = {
    IO {
      // ✅ Force JVM timezone
      System.setProperty("user.timezone", "Asia/Kolkata")
      java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kolkata"))

      println("Timezone: " + java.util.TimeZone.getDefault.getID)

      println("Running Flyway migration...")
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
