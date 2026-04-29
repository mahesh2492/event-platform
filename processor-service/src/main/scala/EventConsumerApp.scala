import cats.effect.{IO, IOApp}
import config.AppConfig
import domain.Event
import fs2.kafka.KafkaConsumer
import infrastucture.KafkaEventConsumer
import service.EventHandlerImpl

object EventConsumerApp extends IOApp.Simple {

  val eventHandler = new EventHandlerImpl[IO]
  val kafkaConsumer = new KafkaEventConsumer(AppConfig.kafkaConfig, eventHandler)
  override def run: IO[Unit] = kafkaConsumer.stream.compile.drain
}
