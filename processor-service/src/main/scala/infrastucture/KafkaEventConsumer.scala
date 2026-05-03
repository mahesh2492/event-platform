package infrastucture

import cats.effect.{Async, Sync}
import cats.implicits._
import config.KafkaConfig
import domain.Event
import fs2.kafka.KafkaConsumer
import cats.Monad
import io.circe.generic.auto._
import io.circe.parser._
import org.slf4j.LoggerFactory
import service.EventHandler

import scala.concurrent.duration.DurationInt

class KafkaEventConsumer[F[_]: Async: Monad](
                                       config: KafkaConfig,
                                       handler: EventHandler[F]) {

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

            Sync[F].delay(logger.info(s"Received record: $record")) *>
              processRecords(record, committable.offset.commit)
          }
      }
  }

  private def processRecords(record: String, commit: F[Unit]): F[Unit] =
    decode[Event](record) match {
      case Right(event) =>
        handler.handle(event) *>
          commit
      case Left(err) =>
        Async[F].delay {
          logger.error(s"Error decoding event: $err")
        } *> commit
    }
}
