#!/bin/bash

echo "🗄️  Setting up ASD Project Database..."

# Try to start MariaDB service
echo "Starting MariaDB service..."
brew services start mariadb 2>/dev/null || mysql.server start 2>/dev/null || echo "MariaDB might already be running"

# Wait a moment for service to start
sleep 2

# Create database and import data
echo "Creating database and importing data..."
mysql -u root -e "CREATE DATABASE IF NOT EXISTS asd;" 2>/dev/null || mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS asd;"

# Import the SQL file
echo "Importing asd.sql..."
mysql -u root asd < asd.sql 2>/dev/null || mysql -u root -p asd < asd.sql

# Test the database
echo "Testing database connection..."
mysql -u root -e "USE asd; SHOW TABLES;" 2>/dev/null || mysql -u root -p -e "USE asd; SHOW TABLES;"

echo "✅ Database setup complete!"
echo ""
echo "📋 Database Info:"
echo "   Database Name: asd"
echo "   Tables: booktime, room"
echo "   Connection: mysql -u root -p asd"
echo ""
echo "🚀 You can now update your Java application to use this database!"