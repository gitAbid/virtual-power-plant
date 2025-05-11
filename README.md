# Virtual Power Plant (VPP) Service

A reactive Spring Boot application for managing virtual power plant batteries, enabling efficient battery registration and querying based on location and capacity parameters.

## Tech Stack

- **Spring Boot 3.4.5**: Reactive web framework with WebFlux
- **Java 21**: Latest LTS version
- **R2DBC with PostgreSQL**: Reactive database connectivity
- **Project Lombok**: Reducing boilerplate code
- **Logback**: Advanced logging framework with async appenders
- **TestContainers 1.21.0**: Integration testing with containerized databases
- **Maven**: Build and dependency management

## Project Structure
```
src/main/java/com/challenge/vpp/
├── config/         # Application configuration
├── controller/     # REST endpoints
├── dto/           # Data transfer objects
├── entity/        # Domain entities
├── exception/     # Custom exceptions
├── repository/    # Database access layer
├── service/       # Business logic
```

## Building and Running

### Prerequisites
- JDK 21
- Docker and Docker Compose
- Maven 3.8+

### Build
```bash
./mvnw clean install
```

### Run
1. Start the PostgreSQL database:
```bash
docker-compose up -d
```
This will start PostgreSQL 17.5 with:
- Database: vpp
- User: vpp_user
- Password: vpp_password
- Port: 5432

2. Run the application:
```bash
./mvnw spring-boot:run
```

## Logging Configuration

The application uses a comprehensive logging setup with:
- Colorized console output for development
- Daily rolling file logs with 30-day retention
- Async logging for improved performance
- Different log levels per component:
  - Application code: DEBUG
  - Spring Framework: INFO
  - R2DBC/Database: INFO

## API Documentation

### 1. Register Batteries
- **Endpoint**: POST /api/v1/vpp/batteries
- **Content-Type**: application/json
- **Request Body**: Array of batteries
```json
[
    {
        "name": "Battery1",
        "postcode": "2000",
        "capacity": 13500.0
    },
    {
        "name": "Battery2",
        "postcode": "2001",
        "capacity": 12000.0
    }
]
```
- **Response**: 201 Created
- **Validation**:
  - name: Required, non-blank
  - postcode: Required, numeric string
  - capacity: Required, positive number

### 2. Query Batteries
- **Endpoint**: GET /api/v1/vpp/batteries
- **Parameters**:
  - startPostcode (required): Starting postcode range
  - endPostcode (required): Ending postcode range
  - minWattCapacity (optional): Minimum watt capacity
  - maxWattCapacity (optional): Maximum watt capacity
- **Response**: 200 OK
```json
{
    "names": [
        "Battery1",
        "Battery2"
    ],
    "totalWattCapacity": 25500.0,
    "averageWattCapacity": 12750.0
}
```
- **Validation**:
  - startPostcode must be less than or equal to endPostcode
  - minWattCapacity and maxWattCapacity must be non-negative
  - if both provided, maxWattCapacity must be greater than or equal to minWattCapacity

## Current Implementation Features

1. **Spring Boot Reactive Stack**
   - Implemented using Spring WebFlux for non-blocking reactive REST API
   - Chosen for superior handling of concurrent battery registrations (Optional Requirement #3)
   - Provides better resource utilization with fewer threads
   - Enables back-pressure management for large data sets

2. **Database Implementation**
   - PostgreSQL with R2DBC (Reactive Database Connectivity)
   - Selected for:
     - ACID compliance for battery data integrity
     - Reactive support through R2DBC for non-blocking operations
     - Excellent performance with range queries (postcode ranges)
     - Built-in support for concurrent operations
   - Implemented repository pattern with reactive operations

3. **API Features**
   - Batch battery registration endpoint with validation
   - Advanced postcode range query with:
     - Alphabetically sorted battery names
     - Total and average watt capacity statistics
     - Optional watt capacity filtering (Optional Requirement #1)
   - Extensive use of Java Streams for:
     - Battery data transformation
     - Statistical calculations
     - Response aggregation

4. **Comprehensive Logging (Optional Requirement #2)**
   - Implemented with Logback framework
   - Features:
     - Colorized console output for development
     - Daily rolling file logs with retention
     - Async logging for performance
     - Contextual logging with different levels:
       - Application code: DEBUG
       - Spring Framework: INFO
       - R2DBC/Database: INFO

5. **Extensive Testing Suite**
   - Achieved >70% test coverage requirement
   - Implemented:
     - Unit tests for service layer logic
     - Integration tests with TestContainers (Optional Requirement #4)
     - Repository tests with containerized PostgreSQL
     - API endpoint tests with WebTestClient

6. **Concurrent Operation Support (Optional Requirement #3)**
   - Reactive programming model for concurrent request handling
   - R2DBC connection pooling for database operations
   - Non-blocking I/O throughout the application stack
   - Async logging to prevent I/O bottlenecks

7. **Input Validation and Error Handling**
   - Comprehensive validation for:
     - Battery registration data
     - Postcode range parameters
     - Watt capacity filters
   - Proper error responses with meaningful messages
   - Exception handling with appropriate HTTP status codes

## Possible Improvements

1. **Security Enhancements**
   - OAuth2/JWT Authentication:
     - Implement role-based access control (RBAC)
     - Secure endpoints with different authorization levels
     - Integrate with OAuth2 providers (e.g., Auth0, Keycloak)
   - API Security:
     - Rate limiting per client/API key
     - Request throttling for DoS protection
     - Input sanitization and validation enhancement
   - Secure Configuration:
     - Externalize sensitive configurations
     - Encrypt sensitive data in database
     - Implement secure password handling

2. **Performance Optimization**
   - Caching Strategy:
     - Redis caching for frequently accessed data
     - Cache postcode range query results
     - Implement cache invalidation policies
   - Database Optimization:
     - Database connection pool tuning
     - Query performance monitoring
   - Application Tuning:
     - Fine-tune reactive operators
     - Optimize memory usage
     - Batch processing improvements

3. **Resilience and Reliability**
   - Circuit Breaker Implementation:
     - Protect database operations
     - Handle external service failures
     - Configure fallback mechanisms
   - Retry Mechanisms:
     - Implement exponential backoff
     - Retry failed database operations
     - Handle transient failures
     
4. **Monitoring and Observability**
   - Metrics Collection:
     - Spring Boot Actuator endpoints
     - Custom metrics for business operations
     - Performance metrics for critical paths
   - Monitoring Integration:
     - Prometheus metric exposition
     - Grafana dashboards for visualization
     - Alert configuration
   - Tracing:
     - Distributed tracing with Zipkin/Jaeger
     - Request correlation
     - Performance bottleneck analysis