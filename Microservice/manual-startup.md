# Manual Startup Guide for Microservices

## Prerequisites
- Java 17 or higher installed
- Maven 3.6+ installed
- Ports 8761, 9001, 9002, 9191, 9296 available

## Startup Order (CRITICAL!)

### 1. Config Server (First - Port 9296)
```bash
cd config-server
mvn spring-boot:run
```
Wait for: "Started ConfigServerApplication"

### 2. Service Registry (Second - Port 8761)
```bash
cd service-registry
mvn spring-boot:run
```
Wait for: "Started ServiceRegistryApplication"
Check: http://localhost:8761 (Eureka Dashboard)

### 3. Customer Service (Third - Port 9001)
```bash
cd customer-service
mvn spring-boot:run
```
Wait for: "Started CustomerServiceApplication"
Check: Service appears in Eureka dashboard

### 4. Order Service (Fourth - Port 9002)
```bash
cd order-service
mvn spring-boot:run
```
Wait for: "Started OrderServiceApplication"
Check: Service appears in Eureka dashboard

### 5. API Gateway (Last - Port 9191)
```bash
cd api-gateway
mvn spring-boot:run
```
Wait for: "Started ApiGatewayApplication"

## Health Checks

### Config Server
```bash
curl http://localhost:9296/actuator/health
```

### Service Registry
```bash
curl http://localhost:8761/eureka/apps
```

### Customer Service (Direct)
```bash
curl http://localhost:9001/customers
```

### Customer Service (Via Gateway)
```bash
curl http://localhost:9191/customers
```

### Order Service (Direct)
```bash
curl http://localhost:9002/orders
```

### Order Service (Via Gateway)
```bash
curl http://localhost:9191/orders
```

## Common Issues

### Port Already in Use
```bash
# Kill process on port
lsof -ti:9296 | xargs kill -9
```

### Service Not Registering
- Check Config Server is running first
- Check Service Registry is accessible
- Verify bootstrap.yml configuration

### Cannot Connect to Customer Service from Order Service
- Ensure both services are registered in Eureka
- Check @LoadBalanced RestTemplate configuration
- Verify service names match Eureka registration

## Testing the Flow

### 1. Create a Customer
```bash
curl -X POST http://localhost:9191/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "address": "123 Main St"
  }'
```

### 2. Create an Order
```bash
curl -X POST http://localhost:9191/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "quantity": 1,
    "price": 999.99,
    "customerId": 1
  }'
```

### 3. Get Order with Customer Details
```bash
curl http://localhost:9191/orders/customer/1
```

This should return combined order and customer information, demonstrating inter-service communication. 