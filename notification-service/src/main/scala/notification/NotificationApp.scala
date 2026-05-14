package notification


import cats.effect.{IO, IOApp}
import config.AppConfig
import infrastructure.kafka.{DlqProducer, DlqProducerResource, KafkaEventConsumer}
import service.NotificationServiceImpl

object NotificationApp extends IOApp.Simple {
  override def run: IO[Unit] =
    DlqProducerResource.create[IO](AppConfig.kafkaProducerConfig).use { kafkaProducer =>
      val dlqProducer = new DlqProducer[IO](kafkaProducer, AppConfig.kafkaProducerConfig)
      val notificationService = new NotificationServiceImpl[IO]
      val kafkaConsumer = new KafkaEventConsumer[IO](AppConfig.kafkaConsumerConfig, notificationService, dlqProducer)
      kafkaConsumer.stream.compile.drain
    }
}
