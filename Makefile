.DEFAULT_GOAL := help

.PHONY: help test test-web run-order run-web lab-up lab-down

help:
	@echo "test      Run all Java service tests"
	@echo "test-web  Run TypeScript, web unit tests, and production build"
	@echo "run-order Start deterministic order service on :4400"
	@echo "run-web   Start operations workspace on :5180"
	@echo "lab-up    Start the full Kafka/PostgreSQL/Redis lab"

test:
	mvn -B test

test-web:
	cd operations-web && npx --yes pnpm@10.6.3 lint && npx --yes pnpm@10.6.3 test && npx --yes pnpm@10.6.3 build

run-order:
	cd order-service && mvn spring-boot:run

run-web:
	cd operations-web && npx --yes pnpm@10.6.3 dev

lab-up:
	docker compose up --build

lab-down:
	docker compose down --volumes --remove-orphans
