package config

final case class KafkaConsumerConfig(
                              bootstrapServers: String,
                              groupId: String,
                              topic: String)

final case class KafkaProducerConfig(
                              bootstrapServers: String,
                              topic: String)
