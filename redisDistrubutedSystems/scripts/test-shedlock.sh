#!/bin/bash

# ShedLock Testing Script
# This script demonstrates ShedLock functionality with multiple scenarios

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BASE_URL_1="http://localhost:8081/api/monitor"
BASE_URL_2="http://localhost:8082/api/monitor"
BASE_URL_3="http://localhost:8083/api/monitor"

echo -e "${BLUE}🔧 ShedLock Testing Script${NC}"
echo -e "${BLUE}===========================${NC}"
echo ""

# Function to print section headers
print_section() {
    echo -e "${YELLOW}📋 $1${NC}"
    echo "----------------------------------------"
}

# Function to check if services are running
check_services() {
    print_section "Checking Service Health"
    
    for port in 8081 8082 8083; do
        url="http://localhost:$port/api/monitor/health"
        echo -n "Instance on port $port: "
        
        if curl -s "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ RUNNING${NC}"
        else
            echo -e "${RED}❌ NOT RUNNING${NC}"
            echo -e "${RED}Please start the services first with: docker-compose up -d${NC}"
            exit 1
        fi
    done
    echo ""
}

# Function to get current locks
show_current_locks() {
    print_section "Current ShedLock Status"
    
    echo "Fetching lock information from instance 1..."
    response=$(curl -s "$BASE_URL_1/locks" || echo '{"error": "failed"}')
    
    if echo "$response" | jq -e '.active_locks' > /dev/null 2>&1; then
        active_locks=$(echo "$response" | jq '.total_locks')
        echo -e "Active locks: ${GREEN}$active_locks${NC}"
        
        if [ "$active_locks" -gt 0 ]; then
            echo "Lock details:"
            echo "$response" | jq '.active_locks' | head -20
        else
            echo "No active locks at the moment"
        fi
    else
        echo -e "${RED}❌ Failed to fetch lock information${NC}"
    fi
    echo ""
}

# Function to show execution history
show_execution_history() {
    print_section "Task Execution History"
    
    for port in 8081 8082 8083; do
        echo -e "${BLUE}Instance on port $port:${NC}"
        url="http://localhost:$port/api/monitor/executions"
        
        response=$(curl -s "$url" || echo '{"error": "failed"}')
        
        if echo "$response" | jq -e '.data_sync_executions' > /dev/null 2>&1; then
            # Show latest execution for each task type
            echo "  📊 Data Sync Executions:"
            echo "$response" | jq -r '.data_sync_executions[0:2][]?' | sed 's/^/    /'
            
            echo "  📈 Report Generations:"
            echo "$response" | jq -r '.daily_reports[0:2][]?' | sed 's/^/    /'
            
            echo "  🧹 Cleanup Operations:"
            echo "$response" | jq -r '.cleanup_executions[0:2][]?' | sed 's/^/    /'
        else
            echo -e "    ${RED}❌ No execution data available${NC}"
        fi
        echo ""
    done
}

# Function to monitor in real-time
monitor_realtime() {
    print_section "Real-time Monitoring (30 seconds)"
    
    echo "Monitoring task executions and locks for 30 seconds..."
    echo "Press Ctrl+C to stop early"
    echo ""
    
    for i in {1..30}; do
        printf "\r${BLUE}⏱️  Monitoring... %d/30 seconds${NC}" $i
        
        # Check for new locks every 5 seconds
        if [ $((i % 5)) -eq 0 ]; then
            echo ""
            response=$(curl -s "$BASE_URL_1/locks" 2>/dev/null || echo '{"total_locks": 0}')
            locks=$(echo "$response" | jq -r '.total_locks // 0')
            
            if [ "$locks" -gt 0 ]; then
                echo -e "${GREEN}🔒 Active locks detected: $locks${NC}"
                echo "$response" | jq -r '.active_locks | to_entries[] | "  - \(.key): TTL \(.value.ttl_seconds)s"'
            else
                echo "🔓 No active locks"
            fi
        fi
        
        sleep 1
    done
    
    echo ""
    echo -e "${GREEN}✅ Monitoring completed${NC}"
    echo ""
}

# Function to test specific task
test_specific_task() {
    local task_name="$1"
    print_section "Testing Task: $task_name"
    
    echo "Fetching details for task: $task_name"
    
    for port in 8081 8082 8083; do
        url="http://localhost:$port/api/monitor/task?taskName=$task_name"
        response=$(curl -s "$url" || echo '{"error": "failed"}')
        
        if echo "$response" | jq -e '.executions' > /dev/null 2>&1; then
            instance=$(echo "$response" | jq -r '.instance_id // "unknown"')
            exec_count=$(echo "$response" | jq -r '.execution_count // 0')
            is_locked=$(echo "$response" | jq -r '.lock_info.is_locked // false')
            
            echo -e "  Instance $port: ${GREEN}$exec_count executions${NC}, Locked: $is_locked"
            
            if [ "$exec_count" -gt 0 ]; then
                echo "    Latest execution:"
                echo "$response" | jq -r '.executions[0]?' | sed 's/^/      /'
            fi
        fi
    done
    echo ""
}

# Function to clear data for fresh testing
clear_test_data() {
    print_section "Clearing Test Data"
    
    echo "Clearing execution history from all instances..."
    
    for port in 8081 8082 8083; do
        url="http://localhost:$port/api/monitor/clear"
        response=$(curl -s "$url" || echo '{"error": "failed"}')
        
        if echo "$response" | jq -e '.status' > /dev/null 2>&1; then
            status=$(echo "$response" | jq -r '.status')
            cleared=$(echo "$response" | jq -r '.cleared_keys')
            echo -e "  Instance $port: ${GREEN}$status${NC} (cleared $cleared keys)"
        else
            echo -e "  Instance $port: ${RED}❌ Failed to clear data${NC}"
        fi
    done
    echo ""
}

# Function to show Redis keys
show_redis_keys() {
    print_section "Redis Keys Analysis"
    
    response=$(curl -s "$BASE_URL_1/redis-keys" || echo '{"error": "failed"}')
    
    if echo "$response" | jq -e '.total_keys' > /dev/null 2>&1; then
        total_keys=$(echo "$response" | jq -r '.total_keys')
        echo -e "Total Redis keys: ${GREEN}$total_keys${NC}"
        
        echo "Key categories:"
        echo "$response" | jq -r '.key_categories | to_entries[] | "  \(.key): \(.value) keys"'
        
        echo ""
        echo "All keys:"
        echo "$response" | jq -r '.all_keys[]?' | head -20 | sed 's/^/  /'
        
        if [ "$total_keys" -gt 20 ]; then
            echo "  ... (showing first 20 keys)"
        fi
    else
        echo -e "${RED}❌ Failed to fetch Redis keys${NC}"
    fi
    echo ""
}

# Main execution
main() {
    echo -e "${GREEN}Starting ShedLock comprehensive testing...${NC}"
    echo ""
    
    # Check if jq is installed
    if ! command -v jq &> /dev/null; then
        echo -e "${RED}❌ jq is required but not installed. Please install jq first.${NC}"
        echo "  Ubuntu/Debian: sudo apt-get install jq"
        echo "  macOS: brew install jq"
        exit 1
    fi
    
    # Run tests
    check_services
    show_current_locks
    show_redis_keys
    show_execution_history
    
    echo -e "${YELLOW}🔍 Want to run specific tests?${NC}"
    echo "1. Monitor real-time (30s)"
    echo "2. Test specific task"
    echo "3. Clear test data"
    echo "4. All of the above"
    echo "5. Exit"
    echo ""
    
    read -p "Enter your choice (1-5): " choice
    
    case $choice in
        1)
            monitor_realtime
            ;;
        2)
            echo "Available tasks: datasync, report, cleanup, health"
            read -p "Enter task name: " task
            test_specific_task "$task"
            ;;
        3)
            clear_test_data
            ;;
        4)
            monitor_realtime
            test_specific_task "datasync"
            test_specific_task "report"
            clear_test_data
            ;;
        5)
            echo -e "${GREEN}✅ Testing completed${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}❌ Invalid choice${NC}"
            exit 1
            ;;
    esac
    
    echo ""
    echo -e "${GREEN}🎉 ShedLock testing completed successfully!${NC}"
    echo ""
    echo -e "${BLUE}📊 Summary:${NC}"
    echo "- Multiple instances are running and competing for locks"
    echo "- Only one instance executes each scheduled task at a time"
    echo "- Redis stores lock information with TTL for automatic cleanup"
    echo "- Task execution history is tracked per instance"
    echo ""
    echo -e "${BLUE}🔍 For continuous monitoring:${NC}"
    echo "- Logs: docker-compose logs -f app-instance-1"
    echo "- Redis Commander: http://localhost:8084"
    echo "- API endpoints: http://localhost:8081/api/monitor/*"
}

# Check if script is being sourced or executed
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi 