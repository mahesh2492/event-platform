package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "localhost:9092",
      topic = "events-topic"
    )
}
