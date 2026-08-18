package com.commerceflow.order;

import com.commerceflow.events.CommerceEvent;

public interface CommerceEventPublisher {
  void publish(CommerceEvent event);
}
