package config

object AppConfig {
  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "localhost:9092",
      groupId = s"notification-group-${scala.util.Random.nextInt()}",
      topic = "events-topic"
    )
}
