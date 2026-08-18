export type OrderStatus = "PENDING_INVENTORY" | "PENDING_PAYMENT" | "COMPLETED" | "REJECTED";

export interface Order {
  orderId: string;
  sku: string;
  quantity: number;
  totalCents: number;
  status: OrderStatus;
  createdAt: string;
}

export interface CommerceEvent {
  eventId: string;
  type: string;
  orderId: string;
  occurredAt: string;
  source: string;
  attributes: Record<string, string>;
}

export interface Overview {
  orders: number;
  completedOrders: number;
  pendingOrders: number;
  events: number;
  messagingMode: string;
  services: string[];
}
