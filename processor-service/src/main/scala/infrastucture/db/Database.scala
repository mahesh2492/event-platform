package infrastucture.db

import cats.effect.kernel.Resource
import cats.effect.{Async, Sync}
import config.DbConfig
import doobie.hikari.HikariTransactor

object Database {

  def transactor[F[_]: Async: Sync](config: DbConfig): Resource[F, HikariTransactor[F]] = {
    for {
      connectEC <- Resource.eval(Async[F].executionContext)
      xa <-  HikariTransactor.newHikariTransactor[F](
        driverClassName = "org.postgresql.Driver",
        url = config.url,
        user = config.userName,
        pass = config.password,
        connectEC = connectEC
      )
    } yield xa

  }

}
