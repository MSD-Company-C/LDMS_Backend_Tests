# LDMS System Test Script
# This script contains manual test cases for the LDMS backend system

## Prerequisites
# 1. Start the Spring Boot application
# 2. Ensure MySQL database is running and configured
# 3. Use a tool like Postman, curl, or any HTTP client

## Test Data Setup
# Make sure you have test users in the database:
# - admin@ldms.com / admin123 (ADMIN role)
# - warehouse@ldms.com / warehouse123 (WAREHOUSE role)  
# - driver@ldms.com / driver123 (DRIVER role)

## Base URL
BASE_URL="http://localhost:8080/api"

## Test Cases

### 1. Authentication Tests

#### Test 1.1: Admin Login (Success)
POST $BASE_URL/auth/login
Content-Type: application/json

{
    "email": "admin@ldms.com",
    "password": "admin123"
}

Expected Response: 200 OK
{
    "token": "jwt-token-here",
    "type": "Bearer"
}

#### Test 1.2: Warehouse Login (Success)
POST $BASE_URL/auth/login
Content-Type: application/json

{
    "email": "warehouse@ldms.com", 
    "password": "warehouse123"
}

Expected Response: 200 OK

#### Test 1.3: Driver Login (Success)
POST $BASE_URL/auth/login
Content-Type: application/json

{
    "email": "driver@ldms.com",
    "password": "driver123"
}

Expected Response: 200 OK

#### Test 1.4: Invalid Email (Failure)
POST $BASE_URL/auth/login
Content-Type: application/json

{
    "email": "nonexistent@ldms.com",
    "password": "password123"
}

Expected Response: 401 Unauthorized
Body: "User not found"

#### Test 1.5: Wrong Password (Failure)
POST $BASE_URL/auth/login
Content-Type: application/json

{
    "email": "admin@ldms.com",
    "password": "wrongpassword"
}

Expected Response: 401 Unauthorized
Body: "Invalid password"

### 2. Driver Management Tests

#### Test 2.1: API Health Check
GET $BASE_URL/drivers/test

Expected Response: 200 OK
Body: "API is working!"

#### Test 2.2: Get All Drivers (Requires ADMIN role)
GET $BASE_URL/drivers
Authorization: Bearer {jwt-token-from-admin-login}

Expected Response: 200 OK (if authenticated as ADMIN)
Expected Response: 403 Forbidden (if not ADMIN role)

### 3. Order Management Tests

#### Test 3.1: Get All Orders (Requires Authentication)
GET $BASE_URL/orders
Authorization: Bearer {jwt-token}

Expected Response: 200 OK (if authenticated with ADMIN/WAREHOUSE/DRIVER)
Expected Response: 401/403 (if not authenticated or wrong role)

#### Test 3.2: Get Order by ID (Requires Authentication)
GET $BASE_URL/orders/1
Authorization: Bearer {jwt-token}

Expected Response: 200 OK (if order exists and user is authenticated)
Expected Response: 404 Not Found (if order doesn't exist)

#### Test 3.3: Update Order Status (Requires ADMIN/WAREHOUSE role)
PUT $BASE_URL/orders/1/status
Authorization: Bearer {jwt-token-admin-or-warehouse}
Content-Type: application/json

{
    "status": "SHIPPED"
}

Expected Response: 200 OK (if user has ADMIN/WAREHOUSE role)
Expected Response: 403 Forbidden (if user has only DRIVER role)

#### Test 3.4: Delete Order
DELETE $BASE_URL/orders/1

Expected Response: 200 OK

### 4. CORS Tests

#### Test 4.1: CORS Preflight Request
OPTIONS $BASE_URL/orders
Origin: http://localhost:3000

Expected Response: 200 OK with appropriate CORS headers

### 5. Security Tests

#### Test 5.1: Access Protected Endpoint without Token
GET $BASE_URL/orders

Expected Response: 401 Unauthorized

#### Test 5.2: Access Protected Endpoint with Invalid Token
GET $BASE_URL/orders
Authorization: Bearer invalid-jwt-token

Expected Response: 401 Unauthorized

### 6. Database Integration Tests

#### Test 6.1: User Creation and Authentication Flow
1. Create a new user in the database
2. Login with the new user credentials
3. Verify JWT token is generated
4. Use the token to access protected endpoints

#### Test 6.2: Order Lifecycle Test
1. Create a new order
2. Update order status from PENDING to PROCESSING
3. Update order status from PROCESSING to SHIPPED
4. Update order status from SHIPPED to DELIVERED
5. Verify order stages are updated correctly

### 7. Performance Tests

#### Test 7.1: Load Test for Authentication
- Send 100 concurrent login requests
- Verify all succeed with correct credentials
- Verify response time is acceptable

#### Test 7.2: Load Test for Order Retrieval
- Send 50 concurrent requests to get all orders
- Verify all responses are successful
- Monitor database connection pool usage

### 8. Error Handling Tests

#### Test 8.1: Malformed JSON Request
POST $BASE_URL/auth/login
Content-Type: application/json

{
    "email": "admin@ldms.com"
    // Missing comma and password field
}

Expected Response: 400 Bad Request

#### Test 8.2: Empty Request Body
POST $BASE_URL/auth/login
Content-Type: application/json

{}

Expected Response: 400 Bad Request or specific validation error

### 9. Integration Tests with Frontend

#### Test 9.1: React Frontend Integration
1. Start React frontend on http://localhost:3000
2. Verify CORS configuration allows frontend requests
3. Test complete login flow from frontend
4. Test order management operations from frontend

### 10. Database State Tests

#### Test 10.1: Verify Database Tables
- Check that all tables are created (users, drivers, orders, etc.)
- Verify foreign key relationships
- Check indexes and constraints

#### Test 10.2: Data Integrity Tests
- Test cascade deletes
- Test unique constraints
- Test null constraints

## Test Results Template

Create a test results document with the following format:

```
Test Case: [Test ID - Test Name]
Date: [Date]
Tester: [Name]
Result: [PASS/FAIL]
Notes: [Any additional observations]
```

## Automated Test Execution

For automated testing, use the following commands:

```bash
# Run all unit tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthControllerTest

# Run tests with coverage
./mvnw test jacoco:report

# Run integration tests
./mvnw test -Dtest=*IntegrationTest
```

## Environment Setup for Testing

### Test Database Configuration
```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Test User Data SQL
```sql
INSERT INTO users (email, password, role) VALUES 
('admin@ldms.com', 'admin123', 'ADMIN'),
('warehouse@ldms.com', 'warehouse123', 'WAREHOUSE'),
('driver@ldms.com', 'driver123', 'DRIVER');
```

This comprehensive test plan covers all major functionality of the LDMS system and should help identify any issues in the implementation.
