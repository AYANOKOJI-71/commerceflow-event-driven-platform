package com.commerceflow.order;

import com.commerceflow.events.CommerceEvent;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${commerce.allowed-origin:http://localhost:5180}")
public class OrderController {
  private final OrderWorkflow workflow;

  public OrderController(OrderWorkflow workflow) {
    this.workflow = workflow;
  }

  @PostMapping("/orders/demo")
  public OrderView createDemoOrder() {
    return workflow.createDemoOrder();
  }

  @GetMapping("/orders")
  public List<OrderView> orders() {
    return workflow.listOrders();
  }

  @GetMapping("/events")
  public List<CommerceEvent> events() {
    return workflow.listEvents();
  }

  @GetMapping("/overview")
  public Map<String, Object> overview() {
    return workflow.overview();
  }
}
