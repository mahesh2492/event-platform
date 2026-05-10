package infrastructure.kafka

import cats.effect.{Async, Resource}
import config.KafkaProducerConfig
import fs2.kafka.{KafkaProducer, ProducerSettings}

object DlqProducerResource {

  def create[F[_]: Async](config: KafkaProducerConfig): Resource[F, KafkaProducer[F, String, String]] = {
    val settings =
      ProducerSettings[F, String, String]
        .withBootstrapServers(config.bootstrapServers)

    KafkaProducer.resource(settings)
  }
}
