#!/bin/bash
# Test script for running all tests

echo "🧪 Running All Tests"
echo "===================="

# Backend tests
echo ""
echo "Running backend tests..."
mvn test

if [ $? -ne 0 ]; then
    echo "❌ Backend tests failed"
    exit 1
fi

echo "✅ Backend tests passed"

# Frontend tests
echo ""
echo "Running frontend tests..."
cd frontend
ng test --watch=false

if [ $? -ne 0 ]; then
    echo "❌ Frontend tests failed"
    exit 1
fi

echo "✅ Frontend tests passed"

echo ""
echo "===================="
echo "✅ All tests passed!"
