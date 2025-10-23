# UniSpace Testing Guide

## Overview
This document describes the comprehensive testing suite for the UniSpace Room Booking System, designed for Azure DevOps CI/CD pipeline integration.

## Test Structure

### Unit Tests (`src/test/java/com/calendar/`)
- **AuthenticationTest.java** - Tests for user authentication logic
- **RoomAvailabilityTest.java** - Tests for room availability servlet
- **BookingSystemTest.java** - Tests for booking workflow and validation
- **SessionManagementTest.java** - Tests for user session handling

### Integration Tests
- **IntegrationTest.java** - End-to-end workflow testing

## Test Configuration

### Dependencies
- JUnit 5 - Testing framework
- Mockito - Mocking framework
- Spring Boot Test - Integration testing support
- H2 Database - In-memory database for testing

### Test Resources
- `application-test.properties` - Test configuration
- `test-accounts.txt` - Test user accounts

## Running Tests

### Local Development
```bash
# Run all unit tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Run all tests with detailed output
./gradlew test integrationTest --info
```

### Azure DevOps Pipeline
Tests are automatically executed in the Azure DevOps pipeline with:
- Detailed reporting
- JUnit XML output
- HTML test reports
- Failure analysis

## Test Coverage

### Authentication Layer
- Valid/invalid login attempts
- Session creation and validation
- Security edge cases

### Room Management
- Available room retrieval
- CORS header validation
- Database connection handling

### Booking System
- End-to-end booking workflow
- JSON request/response validation
- Error handling and edge cases

### Session Management
- User session retrieval
- Session attribute handling
- Authentication state validation

## Azure DevOps Integration

The pipeline includes:
1. **Build Stage** - Compilation and dependency resolution
2. **Test Stage** - Comprehensive unit and integration testing
3. **Security Stage** - Dependency vulnerability scanning
4. **Package Stage** - Application packaging
5. **Deploy Stage** - Staging environment deployment

### Pipeline Features
- Parallel test execution
- Detailed test reporting
- Failure analysis and notifications
- Artifact generation
- Code quality checks

## Test Data Management

### Mock Data
- Test accounts in `test-accounts.txt`
- In-memory H2 database for isolation
- Mockito for servlet and session mocking

### Environment Isolation
- Separate test configuration
- Isolated test database
- Mock external dependencies

## Continuous Integration Benefits

1. **Early Bug Detection** - Catch issues before deployment
2. **Regression Prevention** - Ensure new changes don't break existing functionality
3. **Quality Assurance** - Maintain code quality standards
4. **Automated Validation** - Reduce manual testing overhead
5. **Deployment Confidence** - Reliable automated deployment process

## Adding New Tests

### Unit Tests
1. Create test class in appropriate package
2. Use `@DisplayName` for clear test descriptions
3. Mock external dependencies with Mockito
4. Follow AAA pattern (Arrange, Act, Assert)

### Integration Tests
1. Use `@SpringBootTest` for full application context
2. Test complete user workflows
3. Validate API contracts and responses
4. Include error scenarios

## Troubleshooting

### Common Issues
- **ClassNotFoundException** - Check test dependencies in `build.gradle`
- **Database Connection Errors** - Verify H2 configuration
- **Mock Setup Issues** - Review Mockito configuration

### Pipeline Failures
- Check Azure DevOps test results tab
- Review detailed logs in pipeline execution
- Verify test environment configuration

## Best Practices

1. **Test Isolation** - Each test should be independent
2. **Clear Naming** - Use descriptive test and method names
3. **Comprehensive Coverage** - Test happy path and edge cases
4. **Fast Execution** - Keep tests quick for rapid feedback
5. **Reliable Results** - Avoid flaky tests with proper setup/teardown