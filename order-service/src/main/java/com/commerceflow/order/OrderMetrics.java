package com.commerceflow.order;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {
  private final Counter created;

  public OrderMetrics(MeterRegistry registry) {
    this.created = Counter.builder("commerce_orders_created_total")
        .description("Synthetic orders accepted by the order service")
        .register(registry);
  }

  public void markCreated() {
    created.increment();
  }
}
