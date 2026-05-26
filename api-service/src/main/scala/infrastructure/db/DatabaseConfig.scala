package infrastructure.db

import cats.effect._
import cats.effect.kernel.Resource
import config.AppConfig
import doobie.hikari._

object DatabaseConfig {

  def transactor[F[_]: Async]: Resource[F, HikariTransactor[F]] =
    for {
      connectEC <- Resource.eval(Async[F].executionContext)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url             = AppConfig.dbConfig.url,
        user            = AppConfig.dbConfig.userName,
        pass            = AppConfig.dbConfig.password,
        connectEC = connectEC
      )
    } yield xa
}
