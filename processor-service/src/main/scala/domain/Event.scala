package domain

case class Event(
                  eventId: String,
                  userId: String,
                  eventType: String,
                  timestamp: Long,
                  payload: String
                )
