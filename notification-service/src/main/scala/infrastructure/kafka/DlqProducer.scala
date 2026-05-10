package infrastructure.kafka

import cats.effect.Async
import config.KafkaProducerConfig
import domain.Event
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords}
import io.circe.syntax._
import cats.syntax.all._
import io.circe.generic.auto._


class DlqProducer[F[_]: Async](producer: KafkaProducer[F, String, String], kafkaConfig: KafkaProducerConfig) {

  def publish(event: Event): F[Unit] = {
    val record = ProducerRecord[String, String](
      kafkaConfig.topic,
      event.eventId,
      event.asJson.noSpaces
    )

    val producerRecord = ProducerRecords.one(record)

    producer.
      produce(producerRecord)
      .flatten
      .void
  }

}
