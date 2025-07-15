#!/bin/bash

# Simple Microservices Startup Script
# This script starts services one by one with proper wait times

set -e

echo "🚀 Starting Microservices Architecture..."
echo "=================================="

# Function to check if a port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "❌ Port $port is already in use"
        return 1
    fi
    return 0
}

# Function to wait for service to be ready
wait_for_service() {
    local port=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    echo "Waiting for $service_name on port $port..."
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:$port/actuator/health >/dev/null 2>&1; then
            echo "✅ $service_name is ready!"
            return 0
        fi
        echo "⏳ Attempt $attempt/$max_attempts - waiting for $service_name..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo "❌ $service_name failed to start within timeout"
    return 1
}

# Check required ports
echo "🔍 Checking required ports..."
for port in 9296 8761 9001 9002 9191; do
    if ! check_port $port; then
        echo "Please kill the process using port $port and try again"
        echo "Command: lsof -ti:$port | xargs kill -9"
        exit 1
    fi
done

# Start Config Server
echo "📦 Starting Config Server (Port 9296)..."
cd config-server
nohup mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m" > ../logs/config-server.log 2>&1 &
cd ..
wait_for_service 9296 "Config Server"

# Start Service Registry
echo "📋 Starting Service Registry - Eureka (Port 8761)..."
cd service-registry
nohup mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m" > ../logs/service-registry.log 2>&1 &
cd ..
wait_for_service 8761 "Service Registry"

# Start Customer Service
echo "👥 Starting Customer Service (Port 9001)..."
cd customer-service
nohup mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m" > ../logs/customer-service.log 2>&1 &
cd ..
wait_for_service 9001 "Customer Service"

# Start Order Service
echo "📦 Starting Order Service (Port 9002)..."
cd order-service
nohup mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m" > ../logs/order-service.log 2>&1 &
cd ..
wait_for_service 9002 "Order Service"

# Start API Gateway
echo "🌐 Starting API Gateway (Port 9191)..."
cd api-gateway
nohup mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m" > ../logs/api-gateway.log 2>&1 &
cd ..
wait_for_service 9191 "API Gateway"

echo "🎉 All services started successfully!"
echo "=================================="
echo "📊 Service URLs:"
echo "   • Eureka Dashboard: http://localhost:8761"
echo "   • Config Server: http://localhost:9296"
echo "   • Customer Service: http://localhost:9001/customers"
echo "   • Order Service: http://localhost:9002/orders"
echo "   • API Gateway: http://localhost:9191"
echo "=================================="
echo "🧪 Quick Test Commands:"
echo "   curl http://localhost:9191/customers"
echo "   curl http://localhost:9191/orders"
echo "   curl http://localhost:8761/eureka/apps"
echo "=================================="
echo "📝 Logs are available in the 'logs' directory"
echo "💡 Use './stop-services.sh' to stop all services" 