package domain

import munit.FunSuite

class EventTest extends FunSuite {

  test("valid event passes validation") {
    val event = Event("evt-1", "user-1", "Sign UP", 123456L, "user has signed up")
    val result = event.validate

    assert(result.isRight)
  }

  test("invalid event fails validation") {
    val event = Event("", "user-1", "Sign UP", 123456L, "user has signed up")
    val result = event.validate

    assert(result.isLeft)
  }
}
