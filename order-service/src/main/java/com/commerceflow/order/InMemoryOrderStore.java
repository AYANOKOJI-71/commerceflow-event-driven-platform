package com.commerceflow.order;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "commerce.store.mode", havingValue = "memory", matchIfMissing = true)
public class InMemoryOrderStore implements OrderStore {
  private final Map<UUID, OrderView> orders = new ConcurrentHashMap<>();

  public void save(OrderView order) { orders.put(order.orderId(), order); }

  public Optional<OrderView> find(UUID orderId) { return Optional.ofNullable(orders.get(orderId)); }

  public List<OrderView> list() {
    return orders.values().stream().sorted(Comparator.comparing(OrderView::createdAt).reversed()).toList();
  }
}
