package domain

case class Event(
                  eventId: String,
                  userId: String,
                  eventType: EventType,
                  timestamp: Long,
                  payload: String
                )

sealed trait EventType {
  def value: String
}
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