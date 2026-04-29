package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "localhost:9092",
      groupId = "group-id-new-1",
      topic = "events-topic"
    )
}
