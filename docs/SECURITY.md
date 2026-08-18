# Security and Data Boundaries

CommerceFlow is intentionally a **portfolio laboratory**, not a checkout system. The included demo creates only fixed synthetic SKUs, synthetic totals, generated IDs, and lifecycle timestamps. It stores no personal information, card data, addresses, access tokens, or production logs.

| Boundary | Control in this repository | Production hardening expected |
|---|---|---|
| Event contracts | Versioned event type and schema metadata | Schema registry, compatibility checks, consumer contract tests |
| Payment workflow | Synthetic authorization only; no gateway integration | Tokenized provider SDK, PCI scope assessment, separate secrets |
| Persistence | Environment-provided JDBC parameters | Managed PostgreSQL, TLS, least-privilege service account, backup policy |
| Cache | Redis contains catalog read data only | TLS, authentication, network policy, expiration policy |
| Secrets | `.env.example` contains placeholders only | Kubernetes/managed secret store, rotation, no committed credentials |
| Service access | Local CORS origin is explicit | Gateway authentication, authorization, rate limits, mTLS where justified |

Kafka installations commonly use TLS and SASL for encrypted, authenticated broker connections; this local lab keeps plaintext enabled only to reduce review friction. Those settings must not be copied to a production environment.[1]

## Threat-model Notes

The full lab should be deployed into separate namespaces or environments with service identities that are constrained to the minimum necessary topics. Events should contain opaque identifiers and business facts only. A real order system would also need idempotency persistence, a transactional outbox pattern for database-to-broker consistency, dead-letter handling, PII classification, and audited privileged access.

## References

[1] [Apache Kafka Documentation — Security](https://kafka.apache.org/documentation/#security)
