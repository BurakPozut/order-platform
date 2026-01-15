# Building Microservices - E-Commerce Order Platform

A comprehensive microservices-based e-commerce order management system built with Spring Boot, demonstrating modern distributed system patterns and best practices.

## Overview

This project implements a complete order processing platform using a microservices architecture. The system handles the full order lifecycle from customer creation to order placement, payment processing, inventory management, and notification delivery. It showcases event-driven architecture, distributed transaction management, resilience patterns, and fault tolerance mechanisms.

## Architecture

The system consists of six components: an API Gateway and five independent microservices, each with its own database and responsibility:

- **API Gateway** - Single entry point for all client requests, routing to appropriate services
- **Customer Service** - Manages customer information and profiles
- **Product Service** - Handles product catalog and inventory management
- **Order Service** - Orchestrates order creation and lifecycle management
- **Payment Service** - Processes payments and payment transactions
- **Notification Service** - Sends notifications to customers

Services communicate through:
- **API Gateway** - Routes all external client requests to appropriate microservices
- **Synchronous REST APIs** for direct service-to-service calls
- **Asynchronous Event Streaming** via Kafka/Redpanda for event-driven workflows
- **Common Library** for shared domain models, events, and exceptions

## Technology Stack

### Core Technologies
- **Java 21** - Modern Java features and performance
- **Spring Boot 4.0** - Application framework
- **Spring Data JPA** - Data persistence
- **PostgreSQL 16** - Relational database (one per service)
- **Flyway** - Database migration tool
- **Kafka/Redpanda** - Event streaming platform
- **Docker & Docker Compose** - Containerization and orchestration

### Resilience & Patterns
- **Resilience4j** - Circuit breakers and retry mechanisms
- **Spring WebFlux** - Reactive HTTP client for inter-service communication
- **Spring Kafka** - Event publishing and consumption
- **OpenAPI/Swagger** - API documentation

### Infrastructure
- **Spring Cloud Gateway** - API Gateway for request routing and load balancing
- **Kafka UI** - Web interface for Kafka topic management
- **Health Checks** - Container health monitoring
- **Docker Networking** - Service discovery and communication

## Services

### API Gateway (Port 8090)
Single entry point for all client requests. Routes requests to appropriate microservices based on URL patterns.

**Routes:**
- `/api/customers/**` → Customer Service
- `/api/products/**` → Product Service
- `/api/orders/**` → Order Service
- `/api/payments/**` → Payment Service
- `/api/notifications/**` → Notification Service

**Features:**
- Request routing and load balancing
- Circuit breaker integration for resilience
- Request/response logging
- Health check endpoints
- Swagger/OpenAPI documentation aggregation

**Endpoints:**
- `GET /` - Gateway information and available routes
- `GET /swagger-ui.html` - Aggregated API documentation
- `GET /actuator/health` - Gateway health status
- `GET /actuator/gateway/routes` - List all configured routes

### Customer Service (Port 9081)
Manages customer data and profiles.

**Endpoints:**
- `GET /api/customers` - Get all customers
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create new customer
- `PUT /api/customers/{id}` - Update customer
- `PATCH /api/customers/{id}` - Partially update customer

**Database:** PostgreSQL on port 5435

### Product Service (Port 9082)
Manages product catalog and inventory.

**Endpoints:**
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/name/{name}` - Get product by name
- `PUT /api/products/{id}` - Update product
- `PATCH /api/products/{id}` - Partially update product
- `POST /api/products/{id}/reserve` - Reserve inventory
- `DELETE /api/products/{id}` - Delete product

**Database:** PostgreSQL on port 5436

**Event Listeners:**
- Consumes `OrderCreatedEvent` to reserve inventory
- Publishes compensation events on failure

### Order Service (Port 9083)
Orchestrates order creation and manages order lifecycle.

**Endpoints:**
- `GET /api/orders` - Get all orders (paginated)
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create new order
- `PATCH /api/orders/{id}` - Update order
- `PUT /api/orders/{orderId}/items/{itemId}` - Update order item

**Database:** PostgreSQL on port 5437

**Event Publishers:**
- Publishes `OrderCreatedEvent` when order is created
- Consumes `ServiceCompletionEvent` to track service completions

**Features:**
- Idempotency handling with time-bucketed keys
- Distributed transaction orchestration
- Failed event retry mechanism

### Payment Service (Port 9084)
Handles payment processing and transactions.

**Endpoints:**
- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `POST /api/payments` - Create payment
- `PATCH /api/payments/{id}` - Update payment status

**Database:** PostgreSQL on port 5438

**Event Listeners:**
- Consumes `OrderCreatedEvent` to process payments
- Publishes compensation events on failure

**Features:**
- Idempotency handling
- Circuit breaker for order service calls
- Failed event retry mechanism

### Notification Service (Port 9085)
Manages customer notifications.

**Endpoints:**
- `GET /api/notifications` - Get all notifications
- `GET /api/notifications/{id}` - Get notification by ID
- `POST /api/notifications` - Create notification

**Database:** PostgreSQL on port 5439

**Event Listeners:**
- Consumes `OrderCreatedEvent` to send order confirmation notifications

## Key Features

### Event-Driven Architecture
- Asynchronous communication via Kafka/Redpanda
- Event sourcing for order lifecycle
- Compensation events for failure handling
- Service completion tracking

### Resilience Patterns
- **Circuit Breakers** - Prevents cascading failures
- **Retry Mechanisms** - Automatic retry with exponential backoff
- **Failed Event Handling** - Persistent storage and retry of failed events
- **Idempotency** - Time-bucketed idempotency keys for safe retries

### Distributed Transaction Management
- Saga pattern implementation for distributed transactions
- Compensation transactions for rollback
- Service completion orchestration
- Eventual consistency handling

### Database Per Service
- Each service has its own PostgreSQL database
- Independent schema evolution with Flyway
- Service autonomy and scalability

### Hexagonal Architecture
- Clean separation of concerns
- Domain-driven design principles
- Ports and adapters pattern
- Testable and maintainable code structure

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- Docker and Docker Compose
- 8GB+ RAM (for running all services)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd building-micro-services
```

### 2. Build Common Library

First, build the common library that other services depend on:

```bash
cd common-lib
mvn clean install
cd ..
```

### 3. Start Infrastructure with Docker Compose

Start all databases, Kafka/Redpanda, and Kafka UI:

```bash
docker-compose up -d
```

This will start:
- 5 PostgreSQL databases (one per service)
- Redpanda (Kafka-compatible message broker)
- Kafka UI (accessible at http://localhost:8089)

### 4. Start Services

You can start services individually or all at once. Each service runs on a different port:

**Option 1: Start all services from root directory**
```bash
# Build all services
mvn clean install -DskipTests

# Start each service in separate terminals
cd api-gateway && mvn spring-boot:run
cd customer-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

**Option 2: Use Docker Compose (if services are containerized)**
```bash
docker-compose up
```

### 5. Verify Services

Check service health:
- API Gateway: http://localhost:8090/actuator/health
- Customer Service: http://localhost:9081/actuator/health
- Product Service: http://localhost:9082/actuator/health
- Order Service: http://localhost:9083/actuator/health
- Payment Service: http://localhost:9084/actuator/health
- Notification Service: http://localhost:9085/actuator/health

### 6. Access API Documentation

**Recommended:** Access all APIs through the API Gateway:
- API Gateway (Aggregated): http://localhost:8090/swagger-ui.html

Individual service documentation:
- Customer Service: http://localhost:9081/swagger-ui.html
- Product Service: http://localhost:9082/swagger-ui.html
- Order Service: http://localhost:9083/swagger-ui.html
- Payment Service: http://localhost:9084/swagger-ui.html
- Notification Service: http://localhost:9085/swagger-ui.html

### 7. Access Kafka UI

Kafka UI is available at: http://localhost:8089

## Event Flow

### Order Creation Flow

1. **Client** sends POST request to API Gateway at `/api/orders`
2. **API Gateway** routes request to Order Service
3. **Order Service** validates customer and products via synchronous REST calls
4. **Order Service** creates order and publishes `OrderCreatedEvent` to Kafka
5. **Product Service** consumes event and reserves inventory
6. **Payment Service** consumes event and processes payment
7. **Notification Service** consumes event and sends notification
8. Each service publishes `ServiceCompletionEvent` upon success
9. **Order Service** tracks completions and updates order state
10. **API Gateway** returns response to client

### Compensation Flow

If any service fails:
1. Failed service publishes `OrderCompensationEvent`
2. Other services consume compensation event and rollback their changes
3. Order Service updates order status to reflect failure

## Project Structure

```
building-micro-services/
├── api-gateway/            # API Gateway for request routing
├── common-lib/             # Shared library with events, exceptions, domain models
├── customer-service/       # Customer management service
├── product-service/        # Product catalog and inventory service
├── order-service/          # Order orchestration service
├── payment-service/        # Payment processing service
├── notification-service/   # Notification service
├── docker-compose.yml      # Infrastructure orchestration
└── README.md              # This file
```

Each service follows a similar structure:
```
service-name/
├── src/main/java/
│   └── com/burakpozut/service_name/
│       ├── api/             # REST controllers and DTOs
│       ├── app/             # Application services (use cases)
│       ├── domain/          # Domain models and business logic
│       └── infra/           # Infrastructure adapters (persistence, messaging, external)
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/        # Flyway migrations
└── pom.xml
```

## Configuration

Services can be configured via environment variables or `application.properties`. Key configuration areas:

- **Database URLs**: Set via `SPRING_DATASOURCE_URL`
- **Service URLs**: Set via `*_SERVICE_URL` environment variables
- **Kafka Bootstrap**: Set via `KAFKA_BOOTSTRAP_SERVERS`
- **Logging**: Configure via `LOG_LEVEL_*` environment variables

## Testing

Run tests for all services:

```bash
mvn test
```

Run tests for a specific service:

```bash
cd <service-name>
mvn test
```

## Common Library

The `common-lib` module contains shared code used across all services:

- **Events**: `OrderCreatedEvent`, `OrderCompensationEvent`, `OrderCancelledEvent`, `ServiceCompletionEvent`
- **Exceptions**: Common exception hierarchy for error handling
- **Domain Models**: Shared domain objects like `Currency`, `ServiceName`
- **API Models**: Common API error response format

## Architecture Patterns Implemented

- **Microservices Architecture** - Independent, deployable services
- **Event-Driven Architecture** - Asynchronous event-based communication
- **Saga Pattern** - Distributed transaction management
- **Circuit Breaker Pattern** - Fault tolerance and resilience
- **Retry Pattern** - Automatic failure recovery
- **Idempotency Pattern** - Safe operation retries
- **Database Per Service** - Service data isolation
- **Hexagonal Architecture** - Clean architecture with ports and adapters
- **CQRS** - Command Query Responsibility Segregation (implicit in design)
- **API Gateway Pattern** - Single entry point for all client requests

## Future Enhancements

Potential improvements and extensions:

- Service mesh (Istio/Linkerd) for advanced traffic management
- Distributed tracing (Jaeger/Zipkin)
- Centralized logging (ELK stack)
- Metrics and monitoring (Prometheus/Grafana)
- Authentication and authorization (OAuth2/JWT)
- Rate limiting and throttling
- GraphQL API layer
- Caching layer (Redis)
- Message queue for guaranteed delivery

## License

[Specify your license here]

## Author

[Your name and contact information]
