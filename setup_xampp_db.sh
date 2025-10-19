#!/bin/bash

echo "🚀 Starting XAMPP Database Setup..."

# Start XAMPP MySQL
echo "Starting XAMPP MySQL..."
sudo /Applications/XAMPP/xamppfiles/bin/mysql.server start

# Wait for MySQL to start
sleep 3

# Create database and import
echo "Setting up database..."
/Applications/XAMPP/xamppfiles/bin/mysql -u root -e "CREATE DATABASE IF NOT EXISTS asd;"
/Applications/XAMPP/xamppfiles/bin/mysql -u root asd < asd.sql

echo "✅ Database setup complete!"
echo ""
echo "📋 Database Connection Info:"
echo "   Host: localhost"
echo "   Port: 3306"
echo "   Database: asd"
echo "   Username: root"
echo "   Password: (empty)"
echo ""
echo "🌐 Access phpMyAdmin: http://localhost/phpmyadmin"
echo "🚀 Now run: ./gradlew appRun"