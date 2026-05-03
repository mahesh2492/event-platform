package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "localhost:29092",
      topic = "events-topic"
    )
}
