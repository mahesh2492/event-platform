package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "localhost:9092",
      groupId = s"new-group-id-${scala.util.Random.nextInt()}",
      topic = "events-topic"
    )

  val dbConfig: DbConfig =
    DbConfig(
      url = "jdbc:postgresql://localhost:5432/events_db",
      userName = "postgres",
      password = "postgres"
    )
}
