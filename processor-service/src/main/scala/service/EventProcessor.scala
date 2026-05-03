package service

import cats.effect.Async
import domain.Event
import io.circe.parser.decode
import org.slf4j.LoggerFactory
import cats.implicits._
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

class EventProcessor[F[_]: Async](handler: EventHandler[F]) {
  private val logger = LoggerFactory.getLogger(getClass)

  def processRecords(record: String, commit: F[Unit]): F[Unit] =
    decode[Event](record) match {
      case Right(event) =>
        handler
          .handle(event)
          .handleErrorWith { err =>
            Async[F].delay {
              logger.error(s"Handler failed: $err")
            }
          } *> commit
      case Left(err) =>
        Async[F].delay {
          logger.error(s"Error decoding event: $err")
        } *> commit
    }
}
