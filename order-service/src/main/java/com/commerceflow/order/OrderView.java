package com.commerceflow.order;

import java.time.Instant;
import java.util.UUID;

public record OrderView(UUID orderId, String sku, int quantity, int totalCents, OrderStatus status, Instant createdAt) {}
