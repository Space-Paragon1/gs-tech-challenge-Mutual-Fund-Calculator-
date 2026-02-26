#!/bin/bash
# Script to start both backend and frontend

echo "🚀 Starting Mutual Fund Calculator Application"
echo "=============================================="

# Start backend in background
echo "Starting backend server..."
cd "$(dirname "$0")"
mvn spring-boot:run &
BACKEND_PID=$!

echo "✅ Backend started (PID: $BACKEND_PID)"

# Wait a moment for backend to start
sleep 3

# Start frontend
echo "Starting frontend application..."
cd frontend
npm start &
FRONTEND_PID=$!

echo "✅ Frontend started (PID: $FRONTEND_PID)"

echo ""
echo "=============================================="
echo "Application is running!"
echo ""
echo "Backend:  http://localhost:8080"
echo "Frontend: http://localhost:4200"
echo ""
echo "Press Ctrl+C to stop both servers"
echo ""

# Wait for both processes
wait
