package com.commerceflow.inventory;

import com.commerceflow.events.CommerceEvent;
import com.commerceflow.events.EventType;
import java.util.Map;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservationHandler {
  private final KafkaTemplate<String, CommerceEvent> kafkaTemplate;

  public InventoryReservationHandler(KafkaTemplate<String, CommerceEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @KafkaListener(topics = "commerce.orders.placed.v1", groupId = "inventory-service")
  public void reserve(CommerceEvent orderPlaced) {
    CommerceEvent reserved = CommerceEvent.create(EventType.INVENTORY_RESERVED, orderPlaced.orderId(),
        "inventory-service", Map.of("reservation", "synthetic-reservation", "result", "available"));
    kafkaTemplate.send("commerce.order.events.v1", orderPlaced.orderId().toString(), reserved);
  }
}
