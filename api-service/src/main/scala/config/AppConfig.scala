package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "kafka-server:9092",
      topic = "events-topic"
    )

  val dbConfig: DatabaseConfig =
    DatabaseConfig(
      url = "jdbc:postgresql://postgres:5432/events_db",
      userName = "postgres",
      password = "postgres"
    )
}
