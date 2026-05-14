# Event Platform

A distributed event-driven platform built with Scala, Kafka, PostgreSQL, fs2-kafka, Cats Effect, and http4s.

The platform demonstrates how independent microservices communicate asynchronously using Kafka while persisting and processing events reliably.

---

# Architecture

```text
Client
   |
   v
API Service (http4s)
   |
   v
Kafka Topic (events-topic)
   |
   +----------------------+
   |                      |
   v                      v
Processor Service     Notification Service
   |                      |
   v                      v
PostgreSQL            Notification Handling
```

---

# Services

## API Service

Responsible for:

* Exposing HTTP endpoints
* Accepting incoming events
* Publishing events to Kafka

### Endpoints

#### Health Check

```http
GET /health
```

#### Publish Event

```http
POST /events
Content-Type: application/json
```

Example payload:

```json
{
  "eventId": "evt-4001",
  "userId": "user-9990",
  "eventType": "USER_SIGNUP",
  "timestamp": 1778729007530,
  "payload": "User signup event"
}
```

---

## Processor Service

Responsible for:

* Consuming events from Kafka
* Processing business workflows
* Persisting events into PostgreSQL
* Retry handling
* DLQ publishing

---

## Notification Service

Responsible for:

* Consuming events from Kafka
* Sending notifications
* Handling malformed events gracefully

---

# Tech Stack

* Scala 2.13
* Cats Effect 3
* fs2-kafka
* http4s
* Circe
* PostgreSQL
* Kafka (KRaft mode)
* Docker
* Docker Compose
* HikariCP
* Log4j2

---

# Project Structure

```text
event-platform/
├── api-service/
├── processor-service/
├── notification-service/
├── shared/
├── docker-compose.yml
├── build.sbt
└── README.md
```

---

# Running the Platform

## Start all services

```bash
docker compose up --build
```

---

# Docker Services

| Service     | Port |
| ----------- | ---- |
| API Service | 9000 |
| Kafka       | 9092 |
| PostgreSQL  | 5432 |

---

# Kafka Topics

| Topic            | Purpose                    |
| ---------------- | -------------------------- |
| events-topic     | Main event stream          |
| notification-dlq | Failed notification events |

---

# Important Docker Networking Notes

Inside Docker containers:

* DO NOT use `localhost`
* Use Docker service names instead

Example:

```text
kafka-server:9092
postgres-db:5432
```

Incorrect:

```text
localhost:9092
localhost:5432
```

---

# Kafka Configuration

Kafka runs in KRaft mode using the official Apache Kafka image.

Example configuration:

```yaml
kafka:
  image: apache/kafka:3.7.0
  container_name: kafka-server
  ports:
    - "9092:9092"
  environment:
    - KAFKA_NODE_ID=1
    - KAFKA_PROCESS_ROLES=broker,controller
    - KAFKA_CONTROLLER_QUORUM_VOTERS=1@kafka-server:9093
    - KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://0.0.0.0:9093
    - KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka-server:9092
    - KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER
    - KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
    - KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT
```

---

# SBT Multi-Module Notes

Running a subproject inside Docker requires:

```bash
sbt "apiService/runMain api.Http4sServer"
```

NOT:

```bash
sbt project apiService runMain api.Http4sServer
```

---

# Database Setup

Create database tables manually or via migrations.

Example:

```sql
CREATE TABLE events (
    event_id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    event_type TEXT NOT NULL,
    payload TEXT NOT NULL,
    created_at BIGINT NOT NULL
);
```

---

# Example Test Request

PowerShell:

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:9000/events" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{
    "eventId":"evt-4002",
    "userId":"user-9990",
    "eventType":"USER_SIGNUP",
    "timestamp":1778729007530,
    "payload":"Second test"
  }'
```

---

# Common Issues Faced

## 1. Kafka Connection Refused

Cause:

```text
localhost:9092 used inside containers
```

Fix:

```text
kafka-server:9092
```

---

## 2. PostgreSQL Connection Refused

Cause:

```text
localhost:5432 used inside containers
```

Fix:

```text
postgres-db:5432
```

---

## 3. SBT Command Failing in Docker

Incorrect:

```bash
sbt project apiService runMain api.Http4sServer
```

Correct:

```bash
sbt "apiService/runMain api.Http4sServer"
```

---

## 4. Event Decoding Failure

Example:

```text
Unknown event type: TestEvent
```

Cause:

Mismatch between encoded enum values and decoder values.

---

# Future Improvements

* Schema migrations with Flyway
* Avro/Protobuf schema registry
* Observability with Prometheus/Grafana
* Kubernetes deployment
* Authentication & authorization
* Kafka retries with backoff policies
* OpenTelemetry tracing
* Integration testing with Testcontainers

---

# Learning Goals

This project demonstrates:

* Event-driven architecture
* Distributed systems communication
* Kafka producer/consumer patterns
* Fault tolerance and retries
* Dead-letter queue handling
* Docker networking
* Functional programming with Cats Effect
* Scala microservice design

---
