#!/bin/bash

# ShedLock Demo Startup Script

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🚀 Starting ShedLock Distributed Systems Demo${NC}"
echo "=============================================="
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker first.${NC}"
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null && ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not available.${NC}"
    exit 1
fi

echo -e "${YELLOW}📦 Building and starting services...${NC}"
echo ""

# Build and start services
if command -v docker-compose &> /dev/null; then
    docker-compose up -d --build
else
    docker compose up -d --build
fi

echo ""
echo -e "${GREEN}✅ Services started successfully!${NC}"
echo ""

# Wait for services to be ready
echo -e "${YELLOW}⏳ Waiting for services to be ready...${NC}"
sleep 30

# Check service health
echo ""
echo -e "${BLUE}🏥 Checking service health...${NC}"

for port in 8081 8082 8083; do
    echo -n "  Instance on port $port: "
    if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ READY${NC}"
    else
        echo -e "${YELLOW}⏳ STARTING...${NC}"
    fi
done

echo ""
echo -e "${GREEN}🎉 Demo is ready!${NC}"
echo ""
echo -e "${BLUE}📋 Available Services:${NC}"
echo "  🔧 Application Instance 1: http://localhost:8081"
echo "  🔧 Application Instance 2: http://localhost:8082"  
echo "  🔧 Application Instance 3: http://localhost:8083"
echo "  📊 Redis Commander: http://localhost:8084"
echo ""
echo -e "${BLUE}🔍 Monitoring Endpoints:${NC}"
echo "  📈 Execution History: http://localhost:8081/api/monitor/executions"
echo "  🔒 Active Locks: http://localhost:8081/api/monitor/locks"
echo "  🔑 Redis Keys: http://localhost:8081/api/monitor/redis-keys"
echo "  ❤️  Health Check: http://localhost:8081/api/monitor/health"
echo ""
echo -e "${BLUE}🧪 Testing:${NC}"
echo "  Run: ./scripts/test-shedlock.sh"
echo ""
echo -e "${BLUE}📝 Logs:${NC}"
echo "  All services: docker-compose logs -f"
echo "  Specific instance: docker-compose logs -f app-instance-1"
echo ""
echo -e "${YELLOW}💡 Tip: Watch the logs to see ShedLock in action preventing duplicate executions!${NC}" 