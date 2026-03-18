# User Management System - Makefile

.PHONY: help infra start stop restart logs clean build-backend build-frontend

# Default target
help:
	@echo "User Management System - Available Commands:"
	@echo ""
	@echo "  make infra          - Start PostgreSQL and Redis containers"
	@echo "  make infra-down     - Stop infrastructure containers"
	@echo "  make start          - Start all services (infra + backend + frontend)"
	@echo "  make stop           - Stop all services"
	@echo "  make restart        - Restart all services"
	@echo "  make logs           - View logs from all services"
	@echo "  make logs-backend   - View backend logs"
	@echo "  make logs-db        - View database logs"
	@echo "  make clean          - Stop and remove all containers and volumes"
	@echo "  make build-backend  - Build backend Docker image"
	@echo "  make dev-backend    - Run backend locally with Docker infra"
	@echo "  make dev-frontend   - Run frontend locally"
	@echo "  make test-backend   - Run backend tests"
	@echo "  make test-frontend  - Run frontend tests"
	@echo ""

# Start infrastructure (PostgreSQL + Redis)
infra:
	docker-compose up -d postgres redis
	@echo "Waiting for services to be healthy..."
	@sleep 5
	@docker-compose ps

# Stop infrastructure
infra-down:
	docker-compose down

# Start all services with Docker
start:
	docker-compose --profile full up -d
	@echo "Services started!"
	@echo "Backend: http://localhost:8080/api/v1"
	@echo "Frontend: http://localhost:3000"

# Stop all services
stop:
	docker-compose --profile full down

# Restart all services
restart: stop start

# View logs
logs:
	docker-compose logs -f

logs-backend:
	docker-compose logs -f backend

logs-db:
	docker-compose logs -f postgres

# Clean everything
clean:
	docker-compose --profile full down -v
	docker volume prune -f

# Build backend Docker image
build-backend:
	cd backend && ./mvnw clean package -DskipTests
	docker-compose build backend

# Development commands
dev-backend:
	@echo "Starting infrastructure..."
	@make infra
	@echo "Starting backend locally..."
	cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

dev-frontend:
	cd frontend && npm run dev

# Test commands
test-backend:
	cd backend && ./mvnw test

test-frontend:
	cd frontend && npm test

# Database commands
db-shell:
	docker-compose exec postgres psql -U postgres -d usermanagement

redis-shell:
	docker-compose exec redis redis-cli -a redis123

# Reset database (drop and recreate)
db-reset:
	docker-compose down postgres
	docker volume rm springboot-nextjs-chain03_postgres_data || true
	@make infra
	@echo "Database reset complete. Initializing with seed data..."
