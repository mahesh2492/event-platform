package infrastructure.kafka

import cats.Monad
import cats.effect.{Async, Sync}
import cats.implicits._
import config.KafkaConfig
import domain.Event
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import fs2.kafka.KafkaConsumer
import org.slf4j.LoggerFactory
import service.NotificationServiceImpl

import scala.concurrent.duration._

class KafkaEventConsumer[F[_]: Async: Monad](config: KafkaConfig, notificationService: NotificationServiceImpl[F]) {

  private val logger = LoggerFactory.getLogger(getClass)
  implicit val eventDecoder: Decoder[Event] = deriveDecoder
  implicit val eventEncoder: Encoder[Event] = deriveEncoder
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
            val commit = committable.offset.commit
            Sync[F].delay(logger.info(s"Received record: $record"))

            decode[Event](record) match {
              case Right(event) =>
                notificationService
                  .send(event)
                  .void
                  .handleErrorWith { err =>
                    Async[F].delay {
                      logger.error(s"Handler failed: $err")
                    }
                  } *> commit
              case Left(err) =>
                Async[F].delay {
                  logger.error(s"Error decoding event: $err")
                } *> commit
            }
          }
      }
  }
}
