package com.commerceflow.payment;

import com.commerceflow.events.CommerceEvent;
import com.commerceflow.events.EventType;
import java.util.Map;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentAuthorizationHandler {
  private final KafkaTemplate<String, CommerceEvent> kafkaTemplate;

  public PaymentAuthorizationHandler(KafkaTemplate<String, CommerceEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @KafkaListener(topics = "commerce.order.events.v1", groupId = "payment-service")
  public void authorize(CommerceEvent event) {
    if (event.type() != EventType.INVENTORY_RESERVED) {
      return;
    }
    CommerceEvent authorized = CommerceEvent.create(EventType.PAYMENT_AUTHORIZED, event.orderId(),
        "payment-service", Map.of("paymentReference", "synthetic-payment", "result", "authorized"));
    kafkaTemplate.send("commerce.order.events.v1", event.orderId().toString(), authorized);
  }
}
