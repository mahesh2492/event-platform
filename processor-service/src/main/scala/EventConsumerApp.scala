import cats.effect.{IO, IOApp}
import config.{AppConfig, FlywayMigration}
import domain.Event
import fs2.kafka.KafkaConsumer
import infrastucture.KafkaEventConsumer
import service.{EventHandlerImpl, EventProcessor}

object EventConsumerApp extends IOApp.Simple {

  //Run migration first
  IO {
    FlywayMigration.migrate(AppConfig.dbConfig)
  }

  private val eventHandler = new EventHandlerImpl[IO]
  val processor: EventProcessor[IO] = new EventProcessor[IO](eventHandler)
  private val kafkaConsumer = new KafkaEventConsumer(AppConfig.kafkaConfig, processor)
  override def run: IO[Unit] =
    IO {
      // ✅ Force JVM timezone
      System.setProperty("user.timezone", "Asia/Kolkata")
      java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kolkata"))

      println("Timezone: " + java.util.TimeZone.getDefault.getID)

      println("Running Flyway migration...")
      FlywayMigration.migrate(AppConfig.dbConfig)
    } *>
      kafkaConsumer.stream.compile.drain
}
