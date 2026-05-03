import cats.effect.{IO, IOApp}
import config.AppConfig
import domain.Event
import fs2.kafka.KafkaConsumer
import infrastucture.KafkaEventConsumer
import service.{EventHandlerImpl, EventProcessor}

object EventConsumerApp extends IOApp.Simple {

  private val eventHandler = new EventHandlerImpl[IO]
  val processor: EventProcessor[IO] = new EventProcessor[IO](eventHandler)
  private val kafkaConsumer = new KafkaEventConsumer(AppConfig.kafkaConfig, processor)
  override def run: IO[Unit] = kafkaConsumer.stream.compile.drain
}
