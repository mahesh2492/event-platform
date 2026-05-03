package domain

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

