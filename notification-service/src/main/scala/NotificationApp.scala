
import cats.effect.{IO, IOApp}
import config.AppConfig
import infrastructure.kafka.KafkaEventConsumer
import service.NotificationServiceImpl

object NotificationApp extends IOApp.Simple {

  val notificationService = new NotificationServiceImpl[IO]
  val kafkaConsumer = new KafkaEventConsumer[IO](AppConfig.kafkaConfig, notificationService)
  override def run: IO[Unit] = kafkaConsumer.stream.compile.drain
}
