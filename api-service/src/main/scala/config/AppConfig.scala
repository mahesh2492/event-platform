package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "kafka-server:9092",
      topic = "events-topic"
    )
}
