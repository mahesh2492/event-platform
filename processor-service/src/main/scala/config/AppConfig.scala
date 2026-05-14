package config

object AppConfig {

  val kafkaConfig: KafkaConfig =
    KafkaConfig(
      bootstrapServers = "kafka-server:9092",
      groupId = s"processor-group-${scala.util.Random.nextInt()}",
      topic = "events-topic"
    )

  val dbConfig: DbConfig =
    DbConfig(
      url = "jdbc:postgresql://postgres:5432/events_db",
      userName = "postgres",
      password = "postgres"
    )
}
