package com.commerceflow.order;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class OrderWorkflowTest {
  @Test
  void deterministicFallbackCompletesSyntheticOrderAndRecordsEventTrail() {
    OrderMetrics metrics = new OrderMetrics(new SimpleMeterRegistry());
    OrderWorkflow workflow = new OrderWorkflow(new InMemoryOrderStore(), event -> {}, metrics, "local");

    OrderView order = workflow.createDemoOrder();

    assertThat(order.status()).isEqualTo(OrderStatus.COMPLETED);
    assertThat(workflow.listEvents()).hasSize(5);
    assertThat(workflow.overview()).containsEntry("completedOrders", 1L).containsEntry("events", 5);
  }
}
