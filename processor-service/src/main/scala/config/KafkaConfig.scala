package config

final case class KafkaConfig(
                              bootstrapServers: String,
                              groupId: String,
                              topic: String)
