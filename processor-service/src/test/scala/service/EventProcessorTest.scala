package service

import cats.effect.IO
import domain.Event
import munit.CatsEffectSuite

class EventProcessorTest extends CatsEffectSuite {

  def validJson =
    """{"eventId":"evt-1","userId":"user-1","eventType":"TEST","timestamp":123,"payload":"data"}"""

  test("should call handler for valid event") {

    var called = false

    val handler = new EventHandler[IO] {
      def handle(event: Event): IO[Unit] =
        IO { called = true }
    }

    val processor = new EventProcessor[IO](handler)

    processor.processRecords(validJson, IO.unit).map { _ =>
      assert(called)
    }
  }


  test("should NOT call handler for invalid JSON") {

    var called = false

    val handler = new EventHandler[IO] {
      def handle(event: Event): IO[Unit] =
        IO { called = true }
    }

    val processor = new EventProcessor[IO](handler)

    processor.processRecords("invalid-json", IO.unit).map { _ =>
      assert(!called)
    }
  }

  test("should always commit on valid event") {

    var committed = false

    val handler = new EventHandler[IO] {
      def handle(event: Event): IO[Unit] = IO.unit
    }

    val processor = new EventProcessor[IO](handler)

    val commit = IO { committed = true }

    processor.processRecords(validJson, commit).map { _ =>
      assert(committed)
    }
  }

  test("should always commit on invalid JSON") {
    var committed = false
    val handler = new EventHandler[IO] {
      def handle(event: Event): IO[Unit] = IO.unit
    }

    val processor = new EventProcessor[IO](handler)
    val commit = IO { committed = true }
    processor.processRecords("bad-json", commit).map { _ =>
      assert(committed)
    }
  }

  test("should call handler exactly once") {
    var count = 0
    val handler = new EventHandler[IO] {
      def handle(event: Event): IO[Unit] = IO { count += 1 }
    }

    val processor = new EventProcessor[IO](handler)
    processor.processRecords(validJson, IO.unit).map { _ =>
      assertEquals(count, 1)
    }
  }

  test("should still commit if handler fails") {
    var committed = false

    val handler = new EventHandler[IO] {
      def handle(event: Event): IO[Unit] =
        IO.raiseError(new RuntimeException("boom"))
    }

    val processor = new EventProcessor[IO](handler)

    val commit = IO { committed = true }

    processor.processRecords(validJson, commit).attempt.map { _ =>
      assert(committed)
    }
  }
}
