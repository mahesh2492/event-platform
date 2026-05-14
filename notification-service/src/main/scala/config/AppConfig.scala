package config

object AppConfig {
  val kafkaConsumerConfig: KafkaConsumerConfig =
    KafkaConsumerConfig(
      bootstrapServers = "kafka-server:9092",
      groupId = s"notification-group-${scala.util.Random.nextInt()}",
      topic = "events-topic"
    )

  val kafkaProducerConfig: KafkaProducerConfig =
    KafkaProducerConfig(
      bootstrapServers = "kafka-server:9092",
      topic = "notification-dlq"
    )

}
