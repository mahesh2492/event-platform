package infrastructure

import cats.effect.Async
import fs2.kafka.{AutoOffsetReset, ConsumerSettings}
import config.KafkaConfig

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
