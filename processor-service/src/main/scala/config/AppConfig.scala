package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "localhost:29092",
      groupId = "fresh-group-id",
      topic = "events-topic"
    )
}
