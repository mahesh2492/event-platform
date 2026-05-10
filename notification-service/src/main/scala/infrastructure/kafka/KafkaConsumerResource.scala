package infrastructure.kafka

import cats.effect.Async
import config.KafkaConsumerConfig
import fs2.kafka.{AutoOffsetReset, ConsumerSettings}

object KafkaConsumerResource {

  def create[F[_]: Async](config: KafkaConsumerConfig): ConsumerSettings[F, String, String] = {
    ConsumerSettings[F, String, String]
      .withBootstrapServers(config.bootstrapServers)
      .withGroupId(config.groupId)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withEnableAutoCommit(false)
      .withAllowAutoCreateTopics(true)
  }
}
