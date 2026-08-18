package com.commerceflow.notification;

import com.commerceflow.events.CommerceEvent;
import com.commerceflow.events.EventType;
import java.util.Map;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationHandler {
  private final KafkaTemplate<String, CommerceEvent> kafkaTemplate;

  public NotificationHandler(KafkaTemplate<String, CommerceEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @KafkaListener(topics = "commerce.order.events.v1", groupId = "notification-service")
  public void requestNotification(CommerceEvent event) {
    if (event.type() != EventType.ORDER_COMPLETED) {
      return;
    }
    CommerceEvent requested = CommerceEvent.create(EventType.NOTIFICATION_REQUESTED, event.orderId(),
        "notification-service", Map.of("channel", "simulated", "result", "queued"));
    kafkaTemplate.send("commerce.order.events.v1", event.orderId().toString(), requested);
  }
}
