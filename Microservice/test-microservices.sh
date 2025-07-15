#!/bin/bash

echo "=== Microservices Test Script ==="
echo "Make sure all services are running before executing this script!"
echo ""

# Test Eureka Dashboard
echo "1. Testing Eureka Dashboard..."
curl -s http://localhost:8761 > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Eureka Server is running on port 8761"
else
    echo "❌ Eureka Server is not accessible"
fi

# Test API Gateway
echo ""
echo "2. Testing API Gateway..."
curl -s http://localhost:9191/actuator/health > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ API Gateway is running on port 9191"
else
    echo "❌ API Gateway is not accessible"
fi

# Test Customer Service
echo ""
echo "3. Testing Customer Service..."
echo "Creating a test customer..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:9191/customer/ \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "phone": "123-456-7890",
    "address": "123 Main St, City, State"
  }')

if [[ $CUSTOMER_RESPONSE == *"customerId"* ]]; then
    echo "✅ Customer Service is working - Customer created successfully"
    CUSTOMER_ID=$(echo $CUSTOMER_RESPONSE | grep -o '"customerId":[0-9]*' | grep -o '[0-9]*')
    echo "   Customer ID: $CUSTOMER_ID"
else
    echo "❌ Customer Service failed to create customer"
    echo "   Response: $CUSTOMER_RESPONSE"
fi

# Test Order Service
echo ""
echo "4. Testing Order Service..."
if [ ! -z "$CUSTOMER_ID" ]; then
    echo "Creating a test order for customer $CUSTOMER_ID..."
    ORDER_RESPONSE=$(curl -s -X POST http://localhost:9191/order/ \
      -H "Content-Type: application/json" \
      -d "{
        \"orderName\": \"Test Laptop Purchase\",
        \"orderAmount\": 1299.99,
        \"customerId\": $CUSTOMER_ID
      }")
    
    if [[ $ORDER_RESPONSE == *"orderId"* ]]; then
        echo "✅ Order Service is working - Order created successfully"
        ORDER_ID=$(echo $ORDER_RESPONSE | grep -o '"orderId":[0-9]*' | grep -o '[0-9]*')
        echo "   Order ID: $ORDER_ID"
    else
        echo "❌ Order Service failed to create order"
        echo "   Response: $ORDER_RESPONSE"
    fi
else
    echo "❌ Skipping order test - no customer ID available"
fi

# Test Order with Customer Integration
echo ""
echo "5. Testing Order-Customer Integration..."
if [ ! -z "$ORDER_ID" ]; then
    echo "Fetching order with customer details..."
    INTEGRATION_RESPONSE=$(curl -s http://localhost:9191/order/withCustomer/$ORDER_ID)
    
    if [[ $INTEGRATION_RESPONSE == *"customer"* ]] && [[ $INTEGRATION_RESPONSE == *"order"* ]]; then
        echo "✅ Order-Customer integration is working"
        echo "   Response contains both order and customer data"
    else
        echo "❌ Order-Customer integration failed"
        echo "   Response: $INTEGRATION_RESPONSE"
    fi
else
    echo "❌ Skipping integration test - no order ID available"
fi

echo ""
echo "=== Test Summary ==="
echo "If all tests show ✅, your microservices architecture is working correctly!"
echo ""
echo "You can also check:"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- H2 Console (Customer): http://localhost:9001/h2-console"
echo "- H2 Console (Order): http://localhost:9002/h2-console"
echo "- API Gateway Routes: http://localhost:9191/actuator/gateway/routes" 