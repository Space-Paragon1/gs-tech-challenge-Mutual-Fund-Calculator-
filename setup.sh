#!/bin/bash
# Setup script for Mutual Fund Calculator Application

echo "🚀 Mutual Fund Calculator Setup"
echo "================================"

# Set JAVA_HOME to Java 21 if available in codespace
if [ -d "/home/codespace/java/21.0.9-ms" ]; then
    export JAVA_HOME=/home/codespace/java/21.0.9-ms
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "✅ Using Java 21 from codespace"
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

echo "✅ Java detected: $(java -version 2>&1 | head -n 1)"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.8 or higher."
    exit 1
fi

echo "✅ Maven detected: $(mvn -v | head -n 1)"

# Check if Node is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18 or higher."
    exit 1
fi

echo "✅ Node.js detected: $(node --version)"

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed. Please install npm 9 or higher."
    exit 1
fi

echo "✅ npm detected: $(npm --version)"

# Build backend
echo ""
echo "📦 Building backend..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "❌ Backend build failed"
    exit 1
fi

echo "✅ Backend build completed"

# Setup frontend
echo ""
echo "📦 Setting up frontend..."
cd frontend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
else
    echo "Frontend dependencies already installed"
fi

if [ $? -ne 0 ]; then
    echo "❌ Frontend setup failed"
    exit 1
fi

echo "✅ Frontend setup completed"

echo ""
echo "================================"
echo "✅ Setup completed successfully!"
echo ""
echo "To start the application:"
echo "  Backend:  cd /path/to/gs-tech-challenge && mvn spring-boot:run"
echo "  Frontend: cd frontend && npm start"
echo ""
echo "Or use the start script"
