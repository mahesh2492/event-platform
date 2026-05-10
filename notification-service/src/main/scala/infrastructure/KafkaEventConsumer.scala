package infrastructure

import cats.Monad
import cats.effect.{Async, Sync}
import cats.implicits._
import fs2.kafka.KafkaConsumer
import org.slf4j.LoggerFactory
import config.KafkaConfig
import scala.concurrent.duration._

class KafkaEventConsumer[F[_]: Async: Monad](config: KafkaConfig) {

  private val logger = LoggerFactory.getLogger(getClass)

  def stream: fs2.Stream[F, Unit] = {
    KafkaConsumer
      .stream(KafkaConsumerResource.create(config))
      .flatMap { consumer =>
        fs2.Stream.eval {
          Sync[F].delay(logger.info(s"Subscribing to topic: ${config.topic}")) *>
            consumer.subscribeTo(config.topic) *>
            Async[F].sleep(2.seconds)
        } *>
          consumer.records.evalMap { committable =>
            val record = committable.record.value
            Sync[F].delay(logger.info(s"Received record: $record"))
          }
      }
  }
}
