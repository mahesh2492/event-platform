package config

import org.flywaydb.core.Flyway

object FlywayMigration {

  def migrate(dbConfig: DbConfig): Unit = {
    println("Running Flyway migration...")

    Flyway
      .configure()
      .dataSource(
        dbConfig.url,
        dbConfig.userName,
        dbConfig.password
      )
      .load()
      .migrate()
    println("Flyway migration completed")
  }
}
