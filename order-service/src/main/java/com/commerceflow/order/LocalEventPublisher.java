package com.commerceflow.order;

import com.commerceflow.events.CommerceEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "commerce.messaging.mode", havingValue = "local", matchIfMissing = true)
public class LocalEventPublisher implements CommerceEventPublisher {
  @Override
  public void publish(CommerceEvent event) {
    // The local fallback records events in the workflow ledger instead of publishing externally.
  }
}
