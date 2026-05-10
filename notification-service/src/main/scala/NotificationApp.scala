
import cats.effect.{IO, IOApp}
import config.AppConfig
import infrastructure.KafkaEventConsumer
import org.slf4j.{Logger, LoggerFactory}

object NotificationApp extends IOApp.Simple {

  val kafkaConsumer = new KafkaEventConsumer[IO](AppConfig.kafkaConfig)

  override def run: IO[Unit] = kafkaConsumer.stream.compile.drain
}
