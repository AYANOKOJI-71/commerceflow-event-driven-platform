package com.commerceflow.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderStore {
  void save(OrderView order);

  Optional<OrderView> find(UUID orderId);

  List<OrderView> list();
}
