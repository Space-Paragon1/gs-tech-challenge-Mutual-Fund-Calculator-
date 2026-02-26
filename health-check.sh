#!/bin/bash
# Health check script - verify both backend and frontend are running

echo "🏥 Health Check - Mutual Fund Calculator"
echo "========================================"

# Check backend
echo -n "Backend (http://localhost:8080): "
curl -s http://localhost:8080/api/mutual-funds > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ OK"
else
    echo "❌ FAILED"
fi

# Check frontend
echo -n "Frontend (http://localhost:4200): "
curl -s http://localhost:4200 > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✅ OK"
else
    echo "❌ FAILED"
fi

# Test API endpoint
echo ""
echo "Testing API endpoint..."
echo -n "GET /api/mutual-funds: "
RESPONSE=$(curl -s http://localhost:8080/api/mutual-funds)
if echo "$RESPONSE" | grep -q "VFIAX"; then
    echo "✅ OK"
    echo "Response: $RESPONSE" | head -c 100
    echo "..."
else
    echo "❌ FAILED"
    echo "Response: $RESPONSE"
fi

echo ""
echo "========================================"
