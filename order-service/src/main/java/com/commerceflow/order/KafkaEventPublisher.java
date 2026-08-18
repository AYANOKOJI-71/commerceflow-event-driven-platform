package com.commerceflow.order;

import com.commerceflow.events.CommerceEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "commerce.messaging.mode", havingValue = "kafka")
public class KafkaEventPublisher implements CommerceEventPublisher {
  private static final String ORDER_TOPIC = "commerce.orders.placed.v1";
  private final KafkaTemplate<String, CommerceEvent> kafkaTemplate;

  public KafkaEventPublisher(KafkaTemplate<String, CommerceEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publish(CommerceEvent event) {
    String topic = event.type().name().equals("ORDER_PLACED") ? ORDER_TOPIC : "commerce.order.events.v1";
    kafkaTemplate.send(topic, event.orderId().toString(), event);
  }
}
