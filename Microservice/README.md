# Microservices Architecture with Spring Boot and Spring Cloud

This project demonstrates a complete microservices architecture implementation using Spring Boot, Spring Cloud, and various other technologies.

## 🏗️ Architecture Overview

The project consists of 5 microservices:

1. **Service Registry (Eureka Server)** - Port 8761
2. **Config Server** - Port 9296
3. **API Gateway** - Port 9191
4. **Customer Service** - Port 9001
5. **Order Service** - Port 9002

## 📁 Project Structure

```
Microservice/
├── config-server/          # Centralized configuration management
├── service-registry/       # Eureka server for service discovery
├── customer-service/       # Customer CRUD operations
├── order-service/          # Order CRUD operations + Customer integration
├── api-gateway/           # Request routing and load balancing
└── pom.xml               # Parent POM with dependency management
```

## 🔧 Technologies Used

- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Cloud Config** - Centralized configuration
- **Netflix Eureka** - Service discovery
- **Spring Cloud Gateway** - API gateway
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database
- **Lombok** - Reduce boilerplate code
- **RestTemplate** - Inter-service communication

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### ⚡ Startup Order (IMPORTANT!)

The services must be started in this specific order:

1. **Service Registry** (port 8761)
2. **Config Server** (port 9296)
3. **API Gateway** (port 9191)
4. **Customer Service** (port 9001)
5. **Order Service** (port 9002)

### 🏃‍♂️ Running the Services

#### 1. Service Registry (Eureka Server)
```bash
cd service-registry
mvn spring-boot:run
```
- Access Eureka Dashboard: http://localhost:8761

#### 2. Config Server
```bash
cd config-server
mvn spring-boot:run
```

#### 3. API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

#### 4. Customer Service
```bash
cd customer-service
mvn spring-boot:run
```

#### 5. Order Service
```bash
cd order-service
mvn spring-boot:run
```

### 🔧 Alternative: Build All Services
```bash
# In the root directory
mvn clean install

# Then start each service individually
```

## 📋 API Endpoints

### Through API Gateway (Port 9191)

#### Customer Service APIs
- **POST** `http://localhost:9191/customer/` - Create customer
- **GET** `http://localhost:9191/customer/{id}` - Get customer by ID
- **GET** `http://localhost:9191/customer/` - Get all customers
- **PUT** `http://localhost:9191/customer/{id}` - Update customer
- **DELETE** `http://localhost:9191/customer/{id}` - Delete customer

#### Order Service APIs
- **POST** `http://localhost:9191/order/` - Create order
- **GET** `http://localhost:9191/order/{id}` - Get order by ID
- **GET** `http://localhost:9191/order/` - Get all orders
- **GET** `http://localhost:9191/order/withCustomer/{id}` - Get order with customer details
- **GET** `http://localhost:9191/order/customer/{customerId}` - Get orders by customer ID
- **PUT** `http://localhost:9191/order/{id}` - Update order
- **DELETE** `http://localhost:9191/order/{id}` - Delete order

### Direct Service Access

#### Customer Service (Port 9001)
- **POST** `http://localhost:9001/customer/`
- **GET** `http://localhost:9001/customer/{id}`

#### Order Service (Port 9002)
- **POST** `http://localhost:9002/order/`
- **GET** `http://localhost:9002/order/{id}`
- **GET** `http://localhost:9002/order/withCustomer/{id}`

## 📊 Sample API Calls

### 1. Create a Customer
```bash
curl -X POST http://localhost:9191/customer/ \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "123-456-7890",
    "address": "123 Main St, City, State"
  }'
```

### 2. Create an Order
```bash
curl -X POST http://localhost:9191/order/ \
  -H "Content-Type: application/json" \
  -d '{
    "orderName": "Laptop Purchase",
    "orderAmount": 1299.99,
    "customerId": 1
  }'
```

### 3. Get Order with Customer Details
```bash
curl http://localhost:9191/order/withCustomer/1
```

## 🗃️ Database Access

### H2 Database Consoles
- **Customer Service H2**: http://localhost:9001/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

- **Order Service H2**: http://localhost:9002/h2-console
  - JDBC URL: `jdbc:h2:mem:orderdb`
  - Username: `sa`
  - Password: `password`

## 🔍 Monitoring and Management

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway Actuator**: http://localhost:9191/actuator/gateway/routes

## 🌟 Key Features Implemented

### ✅ Service Discovery
- All services register with Eureka Server
- Dynamic service location and load balancing

### ✅ Centralized Configuration
- Config Server manages all service configurations
- Git-based configuration storage

### ✅ API Gateway
- Single entry point for all client requests
- Request routing based on path patterns
- Load balancing with Ribbon

### ✅ Inter-Service Communication
- Order Service communicates with Customer Service
- Load-balanced RestTemplate calls
- Circuit breaker pattern ready

### ✅ Database per Service
- Each service has its own H2 database
- Data isolation and independence

## 🛠️ Service Details

### Service Registry (Eureka)
- Acts as service discovery server
- All other services register here
- Provides service health monitoring

### Config Server
- Centralized configuration management
- Pulls configurations from Git repository
- Supports environment-specific configs

### Customer Service
- Full CRUD operations for customers
- JPA with H2 database
- RESTful API endpoints

### Order Service
- Full CRUD operations for orders
- Integrates with Customer Service
- Returns combined order and customer data

### API Gateway
- Routes requests to appropriate services
- Load balancing and fault tolerance
- Security and monitoring capabilities

## 🔧 Configuration Notes

- **Bootstrap.yml**: Used for loading configuration from Config Server
- **Application.yml**: Contains service-specific configurations
- **Port Configuration**: Each service runs on a different port
- **Eureka Registration**: All services register with Eureka for discovery

## 🚀 Next Steps

This basic microservices setup can be enhanced with:
- **Spring Security** for authentication and authorization
- **Circuit Breaker** (Hystrix or Resilience4j) for fault tolerance
- **Distributed Tracing** (Zipkin/Sleuth) for request tracking
- **Centralized Logging** (ELK Stack) for log aggregation
- **Docker** containerization for easier deployment
- **Kubernetes** for orchestration and scaling

## 🤝 Contributing

Feel free to fork this project and submit pull requests for improvements!

## 📝 License

This project is open source and available under the [MIT License](LICENSE). 