package service

import cats.effect.Async
import cats.implicits._
import domain.Event
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.{Decoder, Encoder}
import org.slf4j.LoggerFactory

class EventProcessor[F[_]: Async](handler: EventHandler[F]) {
  private val logger = LoggerFactory.getLogger(getClass)

  implicit val eventDecoder: Decoder[Event] = deriveDecoder
  implicit val eventEncoder: Encoder[Event] = deriveEncoder

  def processRecords(record: String, commit: F[Unit]): F[Unit] = {
    logger.info(s"Received records to process $record")
    decode[Event](record) match {
      case Right(event) =>
        handler
          .handle(event)
          .void
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
}
