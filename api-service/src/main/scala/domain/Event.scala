package domain

import io.circe.{Decoder, Encoder}
case class Event(
                eventId: String,
                userId: String,
                eventType: String,
                timestamp: Long,
                payload: String
                ) {
  def validate: Either[String, Event] = {
    if(eventId.isEmpty) Left("eventId can not be empty")
    else if(userId.isEmpty) Left("userId can not be empty")
    else if(timestamp <= 0) Left("Invalid timestamp")
    else Right(this)
  }
}
sealed trait EventType {
  def value: String
}

object EventType {

  case object UserSignup extends EventType {
    val value = "USER_SIGNUP"
  }

  case object UserLogin extends EventType {
    val value = "USER_LOGIN"
  }

  case object Purchase extends EventType {
    val value = "PURCHASE"
  }

  case object PaymentFailed extends EventType {
    val value = "PAYMENT_FAILED"
  }

  case object TestEvent extends EventType {
    val value = "TEST_EVENT"
  }
  def fromString(value: String): Either[String, EventType] =
    value match {
      case "USER_SIGNUP"   => Right(UserSignup)
      case "USER_LOGIN"    => Right(UserLogin)
      case "PURCHASE"      => Right(Purchase)
      case "PAYMENT_FAILED"=> Right(PaymentFailed)
      case "TEST_EVENT"    => Right(TestEvent)
      case other           => Left(s"Unknown event type: $other")
    }

  implicit val decoder: Decoder[EventType] =
    Decoder.decodeString.emap {
      case "USER_SIGNUP" => Right(UserSignup)
      case "USER_LOGIN" => Right(UserLogin)
      case "PURCHASE" => Right(Purchase)
      case "PAYMENT_FAILED" => Right(PaymentFailed)
      case "TEST_EVENT" => Right(TestEvent)
      case other =>
        Left(s"Unknown event type: $other")
    }

  implicit val encoder: Encoder[EventType] =
    Encoder.encodeString.contramap {
      case UserSignup => "USER_SIGNUP"
      case UserLogin => "USER_LOGIN"
      case Purchase => "PURCHASE"
      case PaymentFailed => "PAYMENT_FAILED"
      case TestEvent => "TEST_EVENT"
    }

}
