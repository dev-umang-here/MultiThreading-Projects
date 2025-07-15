#!/bin/bash

echo "=== Microservices Startup Script ==="
echo "This script will start all microservices in the correct order"
echo ""

# Function to wait for a service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    echo "Waiting for $service_name to be ready..."
    while [ $attempt -le $max_attempts ]; do
        if curl -s $url > /dev/null 2>&1; then
            echo "✅ $service_name is ready!"
            break
        fi
        echo "   Attempt $attempt/$max_attempts - waiting..."
        sleep 2
        ((attempt++))
    done
    
    if [ $attempt -gt $max_attempts ]; then
        echo "❌ $service_name failed to start within expected time"
        exit 1
    fi
}

echo "Step 1: Starting Service Registry (Eureka Server)..."
cd service-registry
mvn spring-boot:run > ../logs/eureka.log 2>&1 &
EUREKA_PID=$!
cd ..
wait_for_service "http://localhost:8761" "Service Registry"

echo ""
echo "Step 2: Starting Config Server..."
cd config-server
mvn spring-boot:run > ../logs/config-server.log 2>&1 &
CONFIG_PID=$!
cd ..
wait_for_service "http://localhost:9296/actuator/health" "Config Server"

echo ""
echo "Step 3: Starting API Gateway..."
cd api-gateway
mvn spring-boot:run > ../logs/api-gateway.log 2>&1 &
GATEWAY_PID=$!
cd ..
wait_for_service "http://localhost:9191/actuator/health" "API Gateway"

echo ""
echo "Step 4: Starting Customer Service..."
cd customer-service
mvn spring-boot:run > ../logs/customer-service.log 2>&1 &
CUSTOMER_PID=$!
cd ..
wait_for_service "http://localhost:9001/actuator/health" "Customer Service"

echo ""
echo "Step 5: Starting Order Service..."
cd order-service
mvn spring-boot:run > ../logs/order-service.log 2>&1 &
ORDER_PID=$!
cd ..
wait_for_service "http://localhost:9002/actuator/health" "Order Service"

echo ""
echo "🎉 All services are now running!"
echo ""
echo "Service Status:"
echo "- Service Registry (Eureka): http://localhost:8761 (PID: $EUREKA_PID)"
echo "- Config Server: http://localhost:9296 (PID: $CONFIG_PID)"
echo "- API Gateway: http://localhost:9191 (PID: $GATEWAY_PID)"
echo "- Customer Service: http://localhost:9001 (PID: $CUSTOMER_PID)"
echo "- Order Service: http://localhost:9002 (PID: $ORDER_PID)"
echo ""
echo "Logs are available in the logs/ directory"
echo ""
echo "To test the services, run: ./test-microservices.sh"
echo ""
echo "To stop all services, run: ./stop-all-services.sh"

# Save PIDs for later cleanup
mkdir -p logs
echo $EUREKA_PID > logs/eureka.pid
echo $CONFIG_PID > logs/config-server.pid
echo $GATEWAY_PID > logs/api-gateway.pid
echo $CUSTOMER_PID > logs/customer-service.pid
echo $ORDER_PID > logs/order-service.pid

echo ""
echo "Press Ctrl+C to stop monitoring. Services will continue running in background."
echo "Use ./stop-all-services.sh to stop all services properly."

# Keep script running to monitor
while true; do
    sleep 10
done 