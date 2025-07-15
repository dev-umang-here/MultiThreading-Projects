#!/bin/bash

# Stop Microservices Script

echo "🛑 Stopping Microservices..."
echo "============================"

# Function to kill process on port
kill_port() {
    local port=$1
    local service_name=$2
    
    local pid=$(lsof -ti:$port)
    if [ ! -z "$pid" ]; then
        echo "🔴 Stopping $service_name (Port $port, PID $pid)"
        kill -9 $pid
        echo "✅ $service_name stopped"
    else
        echo "ℹ️  $service_name not running on port $port"
    fi
}

# Stop services in reverse order
kill_port 9191 "API Gateway"
kill_port 9002 "Order Service"
kill_port 9001 "Customer Service"
kill_port 8761 "Service Registry"
kill_port 9296 "Config Server"

# Also kill any mvn spring-boot:run processes
echo "🧹 Cleaning up Maven processes..."
pkill -f "spring-boot:run" 2>/dev/null || echo "No Maven processes found"

echo "✅ All services stopped!"
echo "========================" 