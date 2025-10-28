# UniSpace - University Study Space Booking System
Project associated with UTS

## Project Overview
UniSpace is a web-based application designed to streamline the process of booking study spaces within a university environment. The system allows students and staff to easily find, book, and manage study room reservations while providing administrators with tools to oversee and manage the booking process.

## Repository Structure
```
/app                    # Main application directory
├── data/              # Database files and data storage
├── src/               # Source code
│   ├── main/         
│   │   ├── java/     # Backend Java code
│   │   └── webapp/   # Frontend web assets
│   └── test/         # Test files
├── bin/              # Compiled files
└── build/            # Build outputs

/docs                  # Documentation files
/gradle               # Gradle configuration
```

## Team Members and Contributions

### Core Features and Primary Contributors

#### Authentication & User Management
- **Primary**: Sarah Johnson
- **Secondary**: Michael Chen
- **Files**: `SignupServlet.java`, `LoginServlet.java`, `UserSync.java`
- **Features**: User registration, login system, session management

#### Booking System
- **Primary**: Michael Chen
- **Secondary**: David Wilson
- **Files**: `BookingServlet.java`, `BookingManager.java`, `calendar.js`
- **Features**: Room booking, calendar integration, availability checking

#### Room Management
- **Primary**: David Wilson
- **Secondary**: Emma Thompson
- **Files**: `RoomManager.java`, `FacilityManager.java`
- **Features**: Room creation, facility management, capacity tracking

#### Frontend Interface
- **Primary**: Emma Thompson
- **Secondary**: Sarah Johnson
- **Files**: All files in `webapp/` directory
- **Features**: UI/UX design, responsive layouts, user interaction

#### Testing & Quality Assurance
- **Primary**: All team members
- **Files**: All files in `test/` directory
- **Features**: Unit tests, integration tests, end-to-end testing

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Gradle 7.2 or higher
- MySQL 8.0 or higher
- Node.js 16.x or higher (for frontend development)

### Step 1: Database Setup
1. Install MySQL 8.0
2. Create a new database:
   ```sql
   CREATE DATABASE unispace;
   ```
3. Run the initialization script:
   ```bash
   mysql -u root -p unispace < init-db.sql
   ```

### Step 2: Configuration
1. Copy `application.properties.example` to `application.properties`
2. Update database credentials in `application.properties`
3. Configure server port and other settings as needed

### Step 3: Building the Project
```bash
./gradlew clean build
```

### Step 4: Running the Application
```bash
./gradlew bootRun
```
The application will be available at `http://localhost:8080`

## External Dependencies and Resources

### Frameworks
- Spring Boot 2.7.5
- React 18.2.0
- Material-UI 5.0.0

### External Services
1. **MongoDB Atlas** (Database)
   - Current Plan: Free Tier (M0)
   - Expiration: December 31, 2025
   - Action Required: Migration to paid tier needed before expiration

2. **Redis Cloud** (Caching)
   - Current Plan: 30MB Free Tier
   - Expiration: None
   - Limitations: 30 connections maximum

3. **SendGrid** (Email Service)
   - Current Plan: Free Tier
   - Limitations: 100 emails/day
   - Expiration: None

4. **AWS S3** (File Storage)
   - Current Plan: Free Tier
   - Expiration: November 30, 2025
   - Limitations: 5GB storage

### Notes on External Services
- The MongoDB Atlas free tier expires on December 31, 2025. Before this date, either:
  - Upgrade to a paid tier (recommended)
  - Migrate to a self-hosted MongoDB instance
  - Switch to a different database solution

## Development Workflow
1. Create a new branch for your feature
2. Make your changes
3. Run tests: `./gradlew test`
4. Submit a pull request
5. Get code review and approval

## Common Issues and Solutions
1. **Database Connection Issues**
   - Verify MySQL is running
   - Check credentials in `application.properties`
   - Ensure database exists

2. **Build Failures**
   - Run `./gradlew clean`
   - Update Gradle version
   - Check Java version compatibility

## License
This project is licensed under the MIT License - see the LICENSE file for details
