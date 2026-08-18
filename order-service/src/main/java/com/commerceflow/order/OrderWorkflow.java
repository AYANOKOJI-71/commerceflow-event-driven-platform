package com.commerceflow.order;

import com.commerceflow.events.CommerceEvent;
import com.commerceflow.events.EventType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderWorkflow {
  private final List<CommerceEvent> events = new ArrayList<>();
  private final OrderStore store;
  private final CommerceEventPublisher publisher;
  private final OrderMetrics metrics;
  private final boolean localMode;

  public OrderWorkflow(OrderStore store, CommerceEventPublisher publisher, OrderMetrics metrics,
      @Value("${commerce.messaging.mode:local}") String messagingMode) {
    this.store = store;
    this.publisher = publisher;
    this.metrics = metrics;
    this.localMode = "local".equalsIgnoreCase(messagingMode);
  }

  public synchronized OrderView createDemoOrder() {
    UUID orderId = UUID.randomUUID();
    OrderView accepted = new OrderView(orderId, "SKU-URBAN-MUG", 2, 3998, OrderStatus.PENDING_INVENTORY, Instant.now());
    store.save(accepted);
    metrics.markCreated();
    append(EventType.ORDER_PLACED, accepted, Map.of("sku", accepted.sku(), "quantity", "2", "totalCents", "3998"));
    if (localMode) {
      reserveAndAuthorizeLocally(accepted);
    }
    return store.find(orderId).orElseThrow();
  }

  public synchronized List<OrderView> listOrders() {
    return store.list();
  }

  public synchronized List<CommerceEvent> listEvents() {
    return events.stream().sorted(Comparator.comparing(CommerceEvent::occurredAt).reversed()).toList();
  }

  public synchronized Map<String, Object> overview() {
    List<OrderView> orders = store.list();
    long completed = orders.stream().filter(order -> order.status() == OrderStatus.COMPLETED).count();
    Map<String, Object> view = new LinkedHashMap<>();
    view.put("orders", orders.size());
    view.put("completedOrders", completed);
    view.put("pendingOrders", orders.size() - completed);
    view.put("events", events.size());
    view.put("messagingMode", localMode ? "deterministic-local" : "kafka");
    view.put("services", List.of("order", "catalog", "inventory", "payment", "notification"));
    return view;
  }

  public synchronized void applyExternalEvent(CommerceEvent event) {
    events.add(event);
    OrderView current = store.find(event.orderId()).orElse(null);
    if (current == null) {
      return;
    }
    if (event.type() == EventType.INVENTORY_RESERVED) {
      store.save(withStatus(current, OrderStatus.PENDING_PAYMENT));
    }
    if (event.type() == EventType.PAYMENT_AUTHORIZED) {
      store.save(withStatus(current, OrderStatus.COMPLETED));
      append(EventType.ORDER_COMPLETED, current, Map.of("fulfillment", "ready"));
    }
  }

  private void reserveAndAuthorizeLocally(OrderView order) {
    store.save(withStatus(order, OrderStatus.PENDING_PAYMENT));
    append(EventType.INVENTORY_RESERVED, order, Map.of("reservation", "synthetic-reservation"));
    append(EventType.PAYMENT_AUTHORIZED, order, Map.of("paymentReference", "synthetic-payment"));
    store.save(withStatus(order, OrderStatus.COMPLETED));
    append(EventType.ORDER_COMPLETED, order, Map.of("fulfillment", "ready"));
    append(EventType.NOTIFICATION_REQUESTED, order, Map.of("channel", "simulated"));
  }

  private void append(EventType type, OrderView order, Map<String, String> attributes) {
    CommerceEvent event = CommerceEvent.create(type, order.orderId(), "order-service", attributes);
    events.add(event);
    publisher.publish(event);
  }

  private OrderView withStatus(OrderView order, OrderStatus status) {
    return new OrderView(order.orderId(), order.sku(), order.quantity(), order.totalCents(), status, order.createdAt());
  }
}
