#!/bin/bash

echo "=== Stopping All Microservices ==="

# Function to stop a service by PID
stop_service() {
    local pid_file=$1
    local service_name=$2
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat $pid_file)
        if ps -p $pid > /dev/null 2>&1; then
            echo "Stopping $service_name (PID: $pid)..."
            kill $pid
            sleep 2
            
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                echo "Force stopping $service_name..."
                kill -9 $pid
            fi
            echo "✅ $service_name stopped"
        else
            echo "⚠️  $service_name was not running"
        fi
        rm -f $pid_file
    else
        echo "⚠️  No PID file found for $service_name"
    fi
}

# Stop services in reverse order
if [ -d "logs" ]; then
    stop_service "logs/order-service.pid" "Order Service"
    stop_service "logs/customer-service.pid" "Customer Service"
    stop_service "logs/api-gateway.pid" "API Gateway"
    stop_service "logs/config-server.pid" "Config Server"
    stop_service "logs/eureka.pid" "Service Registry"
else
    echo "No logs directory found. Trying to stop any running Spring Boot processes..."
    
    # Alternative: kill all Java processes running Spring Boot
    pkill -f "spring-boot:run"
    if [ $? -eq 0 ]; then
        echo "✅ Stopped Spring Boot processes"
    else
        echo "⚠️  No Spring Boot processes found"
    fi
fi

echo ""
echo "🛑 All microservices have been stopped"
echo ""
echo "You can verify by checking these URLs (should not respond):"
echo "- http://localhost:8761 (Eureka)"
echo "- http://localhost:9296 (Config Server)"  
echo "- http://localhost:9191 (API Gateway)"
echo "- http://localhost:9001 (Customer Service)"
echo "- http://localhost:9002 (Order Service)" 