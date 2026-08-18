import type { CommerceEvent, Order, Overview } from "./types";

const apiBase = import.meta.env.VITE_COMMERCE_API_URL ?? "";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${apiBase}${path}`, options);
  if (!response.ok) throw new Error(`Operations API returned ${response.status}`);
  return response.json() as Promise<T>;
}

export const commerceApi = {
  overview: () => request<Overview>("/api/overview"),
  orders: () => request<Order[]>("/api/orders"),
  events: () => request<CommerceEvent[]>("/api/events"),
  runDemoOrder: () => request<Order>("/api/orders/demo", { method: "POST" }),
};
