package infrastucture

import cats.effect.{Async, Resource}
import config.KafkaConfig
import fs2.kafka.{AutoOffsetReset, ConsumerSettings, KafkaProducer, ProducerSettings}

object KafkaConsumerResource {

  def create[F[_]: Async](config: KafkaConfig): ConsumerSettings[F, String, String] = {
    println("Creating resources for kafka consumer")
    ConsumerSettings[F, String, String]
        .withBootstrapServers(config.bootstrapServers)
        .withGroupId(config.groupId)
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withEnableAutoCommit(false)
      .withAllowAutoCreateTopics(true)
  }
}
