package infrastructure

import cats.effect.Async
import domain.Event
import fs2.kafka._
import io.circe.syntax._
import cats.syntax.all._
import io.circe.generic.auto._

class KafkaEventProducer[F[_]: Async](
                                       producer: KafkaProducer[F, String, String],
                                       topic: String
                                     ) extends EventProducer[F] {

  override def publish(event: Event): F[Unit] =  {
    val record = ProducerRecord[String, String](
      topic,
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
