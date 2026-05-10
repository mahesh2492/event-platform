package infrastructure.kafka

import cats.effect.Async
import config.KafkaConfig
import fs2.kafka.{AutoOffsetReset, ConsumerSettings}

object KafkaConsumerResource {

  def create[F[_]: Async](config: KafkaConfig): ConsumerSettings[F, String, String] = {
    ConsumerSettings[F, String, String]
      .withBootstrapServers(config.bootstrapServers)
      .withGroupId(config.groupId)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withEnableAutoCommit(false)
      .withAllowAutoCreateTopics(true)
  }
}
