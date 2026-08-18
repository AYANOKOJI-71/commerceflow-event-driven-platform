package com.commerceflow.order;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "commerce.store.mode", havingValue = "postgres")
public class PostgresOrderStore implements OrderStore {
  private final JdbcTemplate jdbc;

  public PostgresOrderStore(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
    jdbc.execute("CREATE TABLE IF NOT EXISTS commerce_orders (order_id UUID PRIMARY KEY, sku VARCHAR(80) NOT NULL, quantity INTEGER NOT NULL, total_cents INTEGER NOT NULL, status VARCHAR(40) NOT NULL, created_at TIMESTAMP NOT NULL)");
  }

  public void save(OrderView order) {
    jdbc.update("INSERT INTO commerce_orders (order_id, sku, quantity, total_cents, status, created_at) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (order_id) DO UPDATE SET status = EXCLUDED.status",
        order.orderId(), order.sku(), order.quantity(), order.totalCents(), order.status().name(), Timestamp.from(order.createdAt()));
  }

  public Optional<OrderView> find(UUID orderId) {
    return jdbc.query("SELECT * FROM commerce_orders WHERE order_id = ?", (rs, row) -> new OrderView(UUID.fromString(rs.getString("order_id")), rs.getString("sku"), rs.getInt("quantity"), rs.getInt("total_cents"), OrderStatus.valueOf(rs.getString("status")), rs.getTimestamp("created_at").toInstant()), orderId).stream().findFirst();
  }

  public List<OrderView> list() {
    return jdbc.query("SELECT * FROM commerce_orders ORDER BY created_at DESC", (rs, row) -> new OrderView(UUID.fromString(rs.getString("order_id")), rs.getString("sku"), rs.getInt("quantity"), rs.getInt("total_cents"), OrderStatus.valueOf(rs.getString("status")), rs.getTimestamp("created_at").toInstant()));
  }
}
