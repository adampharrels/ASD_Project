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

### Features and Contributors

#### Backend Services (Servlets)

##### User Authentication
- `SignupServlet.java` — Adam Nguyen
  - User registration and account creation
- `LoginServlet.java` — Adam Nguyen
  - User authentication and session management
- `UserSync.java` — Adam Nguyen
  - Account synchronization and management

##### Profile Management
- `ProfileServlet.java` — Noah Khuu
  - User profile updates and retrieval
- `ChangePasswordServlet.java` — Noah Khuu & Adam Nguyen
  - Password change functionality

##### Calendar & Booking System
- `BookingServlet.java` — Adam Nguyen & Nathan Nourse
  - Room booking and scheduling
  - Calendar integration
  - Availability checking
Note: Calendar system originally developed by Martin, moved to appropriate folder structure

##### Room Management
- `RoomManager.java` — Nathan Nourse
  - Room creation and management
  - Capacity tracking
- `FacilityManager.java` — Nathan Nourse
  - Facility status and management

#### Frontend Components

##### Home Page
- `home.html`, `home.js`, `styles-home.css` — Noah Khuu
  - Landing page design and implementation
  - User dashboard interface

##### Authentication Pages
- `signup.html`, `script-signup.js` — Adam Nguyen
  - Registration form
  - Client-side validation
- `login.html` — Adam Nguyen
  - Login interface

##### Booking Interface
- `booking.html`, `booking.css` — Nathan Nourse
  - Room booking interface
  - Availability display

##### Calendar Views
- `calendar.js`, Calendar components — Martin
  - Interactive calendar
  - Time slot selection
  - Availability visualization

##### Settings & Profile
- `settings.html`, `settings.css`, `settings.js` — Noah Khuu
  - User preferences
  - Profile management interface

### Testing
Each developer is responsible for testing their own components
- Unit Tests: Respective feature owners
- Integration Tests: Cross-component testing by feature owners

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
