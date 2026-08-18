import { useCallback, useEffect, useState } from "react";
import { Activity, ArrowRight, Boxes, CheckCircle2, CircleDot, CloudCog, LoaderCircle, Radio, RefreshCw, ShieldCheck, ShoppingCart } from "lucide-react";
import { commerceApi } from "./api";
import { money, shortId, time } from "./format";
import type { CommerceEvent, Order, Overview } from "./types";
import "./styles.css";

const stages = ["ORDER_PLACED", "INVENTORY_RESERVED", "PAYMENT_AUTHORIZED", "ORDER_COMPLETED", "NOTIFICATION_REQUESTED"];

export default function App() {
  const [overview, setOverview] = useState<Overview | null>(null);
  const [orders, setOrders] = useState<Order[]>([]);
  const [events, setEvents] = useState<CommerceEvent[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);

  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      const [nextOverview, nextOrders, nextEvents] = await Promise.all([commerceApi.overview(), commerceApi.orders(), commerceApi.events()]);
      setOverview(nextOverview);
      setOrders(nextOrders);
      setEvents(nextEvents);
      setError("");
    } catch {
      setError("The operations API is unavailable. Start the Java order service on port 4400 to inspect the live event trail.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = window.setTimeout(() => void refresh(), 0);
    return () => window.clearTimeout(timer);
  }, [refresh]);

  async function createDemoOrder() {
    setCreating(true);
    try {
      await commerceApi.runDemoOrder();
      await refresh();
    } catch {
      setError("The safe demo order could not be created because the operations API is unavailable.");
    } finally {
      setCreating(false);
    }
  }

  return (
    <main className="app-shell">
      <section className="sidebar">
        <div className="brand"><div className="brand-mark"><Boxes size={20} /></div><span>Commerce<span>Flow</span></span></div>
        <p className="workspace-label">OPERATIONS WORKSPACE</p>
        <nav><a className="active"><Activity size={17} /> Command center</a><a><ShoppingCart size={17} /> Orders</a><a><Radio size={17} /> Event stream</a><a><CloudCog size={17} /> Service topology</a></nav>
        <div className="sidebar-note"><ShieldCheck size={18} /><div><strong>Safe showcase mode</strong><span>No customer, card, or production data.</span></div></div>
      </section>

      <section className="workspace">
        <header className="topbar">
          <div><p className="eyebrow">EVENT-DRIVEN COMMERCE PLATFORM</p><h1>Order command center</h1><p className="subtitle">Trace a synthetic order across service boundaries and asynchronous events.</p></div>
          <div className="header-actions"><button className="icon-button" onClick={() => void refresh()} aria-label="Refresh"><RefreshCw size={18} className={loading ? "spin" : ""} /></button><button className="primary-button" onClick={() => void createDemoOrder()} disabled={creating}><ShoppingCart size={17} />{creating ? "Creating…" : "Run demo order"}</button></div>
        </header>

        {error && <div className="error-banner"><CircleDot size={18} />{error}</div>}

        <section className="metric-grid">
          <Metric label="Orders accepted" value={overview?.orders ?? "—"} detail="Synthetic checkout requests" icon={<ShoppingCart size={20} />} accent="cyan" />
          <Metric label="Completed" value={overview?.completedOrders ?? "—"} detail="Saga workflow succeeded" icon={<CheckCircle2 size={20} />} accent="mint" />
          <Metric label="Event ledger" value={overview?.events ?? "—"} detail="Immutable workflow signals" icon={<Radio size={20} />} accent="violet" />
          <Metric label="Messaging mode" value={overview?.messagingMode === "deterministic-local" ? "LOCAL" : "KAFKA"} detail="Switchable external broker" icon={<CloudCog size={20} />} accent="orange" />
        </section>

        <section className="content-grid">
          <article className="panel journey-panel"><div className="panel-heading"><div><p className="eyebrow">ORDER JOURNEY</p><h2>Asynchronous saga path</h2></div><span className="mode-pill"><span />{overview?.messagingMode ?? "Waiting"}</span></div><div className="journey">{stages.map((stage, index) => <div className="journey-item" key={stage}><div className={events.some(event => event.type === stage) ? "journey-node complete" : "journey-node"}>{events.some(event => event.type === stage) ? <CheckCircle2 size={16} /> : <span>{index + 1}</span>}</div><strong>{stage.replaceAll("_", " ")}</strong><small>{stageService(stage)}</small>{index < stages.length - 1 && <ArrowRight className="journey-arrow" size={16} />}</div>)}</div><div className="diagram-note"><Radio size={15} /> Each stage is a versioned event contract. Kafka mode routes through independently deployable services; local mode preserves the same deterministic evidence chain.</div></article>

          <article className="panel topology-panel"><div className="panel-heading"><div><p className="eyebrow">SERVICE TOPOLOGY</p><h2>Boundary health</h2></div><span className="healthy"><CheckCircle2 size={15} /> nominal</span></div><div className="service-list">{(overview?.services ?? ["order", "catalog", "inventory", "payment", "notification"]).map((service, index) => <div className="service-row" key={service}><div className="service-glyph" data-tone={index}>{service.slice(0, 1).toUpperCase()}</div><div><strong>{service}-service</strong><span>{serviceDescription(service)}</span></div><span className="service-status"><i />READY</span></div>)}</div></article>

          <article className="panel order-panel"><div className="panel-heading"><div><p className="eyebrow">RECENT ORDERS</p><h2>Workflow state</h2></div><span className="count-label">{orders.length} total</span></div><div className="table-wrap"><table><thead><tr><th>Order</th><th>SKU</th><th>Total</th><th>Status</th><th>Created</th></tr></thead><tbody>{orders.length ? orders.map(order => <tr key={order.orderId}><td className="mono">#{shortId(order.orderId)}</td><td>{order.sku}</td><td>{money(order.totalCents)}</td><td><span className={`status ${order.status.toLowerCase()}`}>{order.status.replaceAll("_", " ")}</span></td><td>{time(order.createdAt)}</td></tr>) : <tr><td className="empty-row" colSpan={5}>No synthetic orders yet. Run the safe demo to start the journey.</td></tr>}</tbody></table></div></article>

          <article className="panel event-panel"><div className="panel-heading"><div><p className="eyebrow">EVENT LEDGER</p><h2>Latest signals</h2></div><span className="live-label"><i /> LIVE</span></div><div className="event-list">{events.length ? events.slice(0, 8).map(event => <div className="event-row" key={event.eventId}><div className="event-dot" /><div><strong>{event.type.replaceAll("_", " ")}</strong><span>{event.source} · #{shortId(event.orderId)}</span></div><time>{time(event.occurredAt)}</time></div>) : <div className="empty-events"><LoaderCircle size={22} /> Awaiting event stream</div>}</div></article>
        </section>
      </section>
    </main>
  );
}

function Metric({ label, value, detail, icon, accent }: { label: string; value: string | number; detail: string; icon: React.ReactNode; accent: string }) {
  return <article className={`metric-card ${accent}`}><div className="metric-icon">{icon}</div><div><p>{label}</p><strong>{value}</strong><span>{detail}</span></div></article>;
}

function stageService(stage: string): string {
  return { ORDER_PLACED: "order-service", INVENTORY_RESERVED: "inventory-service", PAYMENT_AUTHORIZED: "payment-service", ORDER_COMPLETED: "order-service", NOTIFICATION_REQUESTED: "notification-service" }[stage] ?? "service";
}

function serviceDescription(service: string): string {
  return { order: "Saga orchestrator", catalog: "Cached product reads", inventory: "Reservation consumer", payment: "Synthetic authorization", notification: "Delivery event worker" }[service] ?? "Service boundary";
}
