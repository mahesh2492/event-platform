package infrastructure

import cats.effect.{Async, Resource}
import config.KafkaConfig
import fs2.kafka.{KafkaProducer, ProducerSettings}

object KafkaProducerResource {

  def create[F[_]: Async](config: KafkaConfig): Resource[F, KafkaProducer[F, String, String]] = {
    val settings =
      ProducerSettings[F, String, String]
        .withBootstrapServers(config.bootstrapServers)

    KafkaProducer.resource(settings)
  }

}
