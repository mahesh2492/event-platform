# Event Platform

## 📌 Overview

Event Platform is a distributed system that processes events asynchronously using **Apache Kafka** and persists them into **PostgreSQL**.

The system is designed with a clear separation of concerns:

* **API Service** → Produces events to Kafka
* **Processor Service** → Consumes events and processes them
* **PostgreSQL** → Stores processed events

---

## 🏗️ Architecture

```
Client → API Service → Kafka → Processor Service → PostgreSQL
```

### Flow

1. Client sends event to API
2. API publishes event to Kafka topic
3. Processor service consumes event
4. Event is processed and stored in Postgres
5. Offset is committed only after successful processing

---

## 🧩 Services

### 1. API Service

* Accepts HTTP requests
* Validates event
* Publishes to Kafka

### 2. Processor Service

* Consumes events from Kafka
* Decodes JSON into domain model
* Applies business logic
* Persists event to database

---

## ⚙️ Tech Stack

* Scala (Cats Effect, FS2)
* Kafka (KRaft mode)
* PostgreSQL
* Doobie (DB access)
* Circe (JSON)

---

## 📁 Project Structure

### Processor Service

```
processor-service
├── config
│   ├── AppConfig
│   └── KafkaConfig
│
├── domain
│   └── Event
│
├── infrastructure
│   ├── KafkaConsumerResource
│   └── KafkaEventConsumer
│
├── service
│   ├── EventHandler
│   ├── EventHandlerImpl
│   └── EventProcessor
```

---

## 🚀 Getting Started

### 1. Start Infrastructure

```bash
docker-compose up -d
```

Services started:

* Kafka → localhost:9092
* Postgres → localhost:5432

---

### 2. Create Database Table

```sql
CREATE TABLE events (
  event_id TEXT PRIMARY KEY,
  user_id TEXT,
  event_type TEXT,
  timestamp BIGINT,
  payload TEXT
);
```

---

### 3. Run Services

#### API Service

```bash
sbt "project apiService" run
```

#### Processor Service

```bash
sbt "project processorService" run
```

---

## 🧪 Sending Test Event

### PowerShell

```powershell
$body = @{
  eventId   = "evt-1"
  userId    = "user-1"
  eventType = "TEST_EVENT"
  timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
  payload   = "Test event"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:9000/events" `
  -Method Post `
  -Body $body `
  -ContentType "application/json"
```

---

## 🔍 Verify Data

```bash
docker exec -it postgres-db psql -U postgres -d events_db -c "SELECT * FROM events;"
```

---

## ✅ Processing Guarantees

* **At-least-once delivery**
* Offset committed only after successful DB write
* Invalid JSON messages are skipped safely

---

## ⚠️ Known Limitations

* No retry mechanism (yet)
* No dead-letter queue (DLQ)
* No schema migration tool (manual SQL)
* No idempotency beyond primary key constraint

---

## 🔮 Future Improvements

* Retry with exponential backoff
* Dead Letter Queue (DLQ)
* Flyway for schema migrations
* Event-type based routing
* Integration tests with Testcontainers

---

## 🧠 Design Principles

* Separation of infrastructure and business logic
* Functional effect handling using Cats Effect
* Testable components (EventProcessor, EventHandler)
* Minimal side effects

---

## 👨‍💻 Author

Event Platform Project
