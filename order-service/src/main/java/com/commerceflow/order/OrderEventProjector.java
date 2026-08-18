package com.commerceflow.order;

import com.commerceflow.events.CommerceEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "commerce.messaging.mode", havingValue = "kafka")
public class OrderEventProjector {
  private final OrderWorkflow workflow;

  public OrderEventProjector(OrderWorkflow workflow) {
    this.workflow = workflow;
  }

  @KafkaListener(topics = "commerce.order.events.v1", groupId = "order-service")
  public void project(CommerceEvent event) {
    workflow.applyExternalEvent(event);
  }
}
