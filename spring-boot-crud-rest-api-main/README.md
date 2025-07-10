# Spring Boot REST API (CRUD Operation)

A RESTful API built with Spring Boot for managing products. This API provides endpoints for creating, reading, updating, and deleting product information.

## Features

- CRUD operations for products
- Category-based product filtering
- Price-based product filtering
- Name-based product search
- Global exception handling
- Input validation
- MySQL database integration

## Technologies

- Java 21
- Spring Boot 3.4.5
- Spring Data JPA
- MySQL
- Maven
- Jakarta Validation

## Prerequisites

- JDK 21 or later
- Maven 3.9+
- MySQL Server

## Database Configuration

The application is configured to connect to a MySQL database. Update the following properties in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=MYSQL DATABASE URL
spring.datasource.username=DATABASE USERNAME
spring.datasource.password=DATABASE PASSWORD
```

## Building and Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```bash
   ./mvnw clean install
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on port 8080.

## API Endpoints

### Products

- GET `/api/products` - Get all products
- GET `/api/products/{id}` - Get a specific product by ID
- GET `/api/products/category/{categoryName}` - Get products by category
- GET `/api/products/price/{maxPrice}` - Get products with price less than or equal to maxPrice
- GET `/api/products/search?name={name}` - Search products by name
- POST `/api/products` - Create a new product
- PUT `/api/products/{id}` - Update an existing product
- DELETE `/api/products/{id}` - Delete a product

### Request/Response Examples

#### Product Object Structure
```json
{
    "id": 1,
    "name": "Product Name",
    "description": "Product Description",
    "price": 99.99,
    "category": "Category Name"
}
```

## Error Handling

The API includes global exception handling for:
- Resource not found exceptions
- Validation errors
- General server errors

## Validation

The API validates product input with the following rules:
- Product name cannot be blank
- Product price must be positive

## Development

The project uses the following folder structure:
```
src/main/java/
    └── com.learnwithiftekhar.spring_boot_rest_api/
        ├── controller/      # REST controllers
        ├── model/          # Data models
        ├── repository/     # Data access layer
        ├── service/        # Business logic
        └── exception/      # Exception handling
```