# CommerceFlow — Event-Driven E-Commerce Microservices Platform

CommerceFlow is an interview-ready **Java 21 / Spring Boot** microservices laboratory that demonstrates asynchronous order processing, versioned Kafka events, service-owned persistence, Redis-backed catalog caching, and Kubernetes deployment intent. It contains no production orders, customer identities, payment credentials, or external payment integration.

The live review path is intentionally deterministic: a safe synthetic checkout produces a five-step saga trail that can be inspected in the React operations workspace. The full container topology switches the same boundaries to Kafka, PostgreSQL, and Redis.

## What It Demonstrates

| Capability | Implementation |
|---|---|
| Order saga | `ORDER_PLACED → INVENTORY_RESERVED → PAYMENT_AUTHORIZED → ORDER_COMPLETED → NOTIFICATION_REQUESTED` |
| Microservice boundaries | Spring Boot order, catalog, inventory, payment, and notification services |
| Event contracts | Shared, versioned `CommerceEvent` envelope and explicit producer metadata |
| Resilient local review | In-memory repository and local event publisher without Docker or a broker |
| Full infrastructure lab | Kafka KRaft, PostgreSQL, Redis, Compose, health endpoints, Docker multi-stage images |
| Operations visibility | React command center for orders, ledger signals, topology, and workflow status |
| Kubernetes intent | Deployments, probes, resource requests, HPA, ConfigMap, and placeholder Secret manifest |

## Quick Start

```bash
# Terminal 1: deterministic Java API
make run-order

# Terminal 2: React operations workspace
make run-web

# Validation
make test
make test-web
```

Visit `http://localhost:5180` and select **Run demo order**. The workspace will show the generated synthetic order, immutable event sequence, and completed saga path.

## Full Local Lab

```bash
docker compose up --build
```

The full lab uses local-only container credentials and is not a production deployment. See [Architecture](docs/ARCHITECTURE.md), [security boundaries](docs/SECURITY.md), and the [safe demo guide](docs/DEMO.md) before changing modes.

## Repository Layout

```text
common-events/        Shared event contract
order-service/        Command API, order workflow, persistence adapter
catalog-service/      Synthetic catalog and Redis cache boundary
inventory-service/    Kafka reservation consumer
payment-service/      Kafka synthetic authorization consumer
notification-service/ Kafka completion consumer
operations-web/       React operations workspace
docker/               Container image definitions and Nginx proxy
k8s/                  Kubernetes deployment intent
docs/                 Architecture, security, and demo guidance
```

## Quality Gate

GitHub Actions executes the Java service tests plus TypeScript checks, frontend unit tests, and a production web build for every pull request and push to `main`. The workflow uses Temurin Java 21 and Node 22.

## Interview Discussion Prompts

CommerceFlow is designed to support a detailed discussion of event versioning, idempotency, saga coordination, transactional-outbox adoption, read-cache ownership, retry and dead-letter policy, service health probes, autoscaling, and the intentional distinction between a local deterministic mode and a production integration mode.
