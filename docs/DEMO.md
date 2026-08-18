# Deterministic Demonstration

Run the following two processes in separate terminals. This mode needs no containers, brokers, database instance, cache, credentials, or external service.

```bash
make run-order
make run-web
```

Open `http://localhost:5180`, select **Run demo order**, and observe the event ledger. The safe scenario consistently emits this sequence:

1. `ORDER_PLACED`
2. `INVENTORY_RESERVED`
3. `PAYMENT_AUTHORIZED`
4. `ORDER_COMPLETED`
5. `NOTIFICATION_REQUESTED`

For the complete infrastructure exercise, copy `.env.example`, confirm that all values are local-only placeholders, then run `docker compose up --build`. The compose stack exposes the operations console on port `5180`, the order API on `4400`, Kafka on `9092`, PostgreSQL on `5434`, and Redis on `6380`.

> The sandbox used for this portfolio build does not provide a Docker runtime. Compose and Kubernetes files are included for local or CI-capable environments, while the deterministic workflow is the verified review path.
