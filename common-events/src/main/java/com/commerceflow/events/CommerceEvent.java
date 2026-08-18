package com.commerceflow.events;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CommerceEvent(
    UUID eventId,
    EventType type,
    UUID orderId,
    Instant occurredAt,
    String source,
    Map<String, String> attributes
) {
  public static CommerceEvent create(EventType type, UUID orderId, String source, Map<String, String> attributes) {
    return new CommerceEvent(UUID.randomUUID(), type, orderId, Instant.now(), source, Map.copyOf(attributes));
  }
}
