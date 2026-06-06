package metrics

import io.prometheus.client.{Counter, Histogram}

object Metrics {

  val eventsProcessed: Counter =
    Counter.build()
      .name("events_processed_total")
      .help("Total processed events")
      .register()

  val processingFailures: Counter =
    Counter.build()
      .name("event_processing_failures_total")
      .help("Total failed events")
      .register()

  val processingLatency: Histogram =
    Histogram.build()
      .name("event_processing_latency_seconds")
      .help("Event processing latency")
      .register()
}