package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "localhost:29092",
      groupId = "fresh-group-id",
      topic = "events-topic"
    )

  val dbConfig: DbConfig =
    DbConfig(
      url = "jdbc:postgresql://localhost:5432/events_db?options=-c%20TimeZone=Asia/Kolkata",
      userName = "postgres",
      password = "postgres"
    )
}
