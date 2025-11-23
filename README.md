📘 Employee API – Coding Challenge Solution

This project implements a Spring Boot Employee API http://localhost:8111/api/v2/employee that integrates with a Mock Employee Server http://localhost:8112/api/v1/employee. via REST calls.
It demonstrates clean coding principles, REST API best practices, validation, exception handling, and test-driven development.

📂 Project Structure
root/
├── api/                 # Main Spring Boot application
│    ├── controller/     # REST controllers
│    ├── service/        # Business logic
│    ├── client/         # RestTemplate client for downstream API
│    ├── dto/            # Request DTOs with validation
│    ├── model/          # API response & Employee model
│    ├── error/          # Global exception handler
│    ├── test/           # Unit & integration tests
│    └── ApiApplication
└── server/              # Mock Employee API (assignment-provided)

🚀 Features Implemented
✔ REST Endpoints http://localhost:8111/api/v2/employee
Endpoint	Description
GET /api/v2/employee	Returns all employees
GET /api/v2/employee/search/{name}	Search employee by name fragment
GET /api/v2/employee/{id}	Get employee by ID
GET /api/v2/employee/highestSalary	Returns the highest employee salary
GET /api/v2/employee/topTenHighestEarningEmployeeNames	Returns top 10 employees by salary
POST /api/v2/employee	Creates a new employee
DELETE /api/v2/employee/{name}	Deletes employee by name
🧱 Core Concepts Demonstrated
✅ 1. Input Validation (Bean Validation)

Each request is validated using jakarta.validation:

@NotBlank(message = "Name must not be blank")
@Size(min = 3, message = "name must be at least 3 characters")
private String name;


Controller uses:

public ResponseEntity<Employee> createEmployee(
@Valid @RequestBody CreateEmployeeRequest request)


Invalid inputs return:

400 Bad Request


With a clean JSON error message.

✅ 2. Global Exception Handling

A centralized @RestControllerAdvice converts exceptions into structured JSON:

MethodArgumentNotValidException → 400

IllegalArgumentException → 400

HttpClientErrorException.TooManyRequests → 429

Graceful downstream parsing of Mock Server validation errors

Example error JSON:

{
"timestamp": "2025-11-23T14:00:00Z",
"status": 400,
"error": "Bad Request",
"message": "name must be at least 3 characters",
"path": "/api/v2/employee"
}

✅ 3. Custom Downstream Error Translation

Mock server sometimes returns large 500 error blobs containing validation messages.
Our client extracts only useful messages via regex:

default message [must be greater than or equal to 16]


These are turned into:

400 Bad Request
{
"message": "must be greater than or equal to 16"
}

✅ 4. Unit Testing & Integration Testing

A comprehensive suite includes:

✔ Controller tests with MockMvc

Validation scenarios:

null / blank / whitespace

minimum length

min/max age

min salary

multiple errors

✔ Integration tests using MockRestServiceServer

Verifies:

Downstream 500 validation → transformed to 400

Successful creation flow

Downstream matching stubs

▶️ Running the Application
1. Start the Mock Server

Inside root folder:

cd server
./gradlew bootRun


Mock server listens on:

http://localhost:8112/api/v1/employee

2. Start the API module
   cd api
   ./gradlew bootRun


API listens on:

http://localhost:8111/api/v2/employee

🧪 Running Tests

Inside the api module:

./gradlew test


Test coverage includes:

Controller + validation

Service logic

API client behavior

Downstream error translation

Integration-level flows

🔧 Configuration
application.yml
server:
port: 8111

spring:
application:
name: employee-api

employee:
mock:
base-url: http://localhost:8112/api/v1/employee


Override base URL for testing:

application-test.yml:

employee:
mock:
base-url: http://localhost:9999/api/v1/employee

📡 API Client

The client uses:

RestTemplate

Connection & read timeouts

JSON parsing

Downstream validation extraction

catch (HttpServerErrorException ex) {
String cleaned = extractValidationMessage(ex.getResponseBodyAsString());
throw new IllegalArgumentException(cleaned);
}

📈 Scalability & Resilience Considerations

Implemented / recommended:

✔ Local validation to reduce unnecessary downstream calls
✔ Downstream error translation for clean API responses
✔ Timeout configuration on RestTemplate
✔ CircuitBreaker (if needed) – optional
✔ Thread safety and stateless service layer
✔ Clean DTO layer separation

Future enhancements:

Replace RestTemplate with WebClient (reactive, non-blocking)

Use Resilience4j Retry + RateLimiter if required

Introduce caching for high-read endpoints

🏗 Architecture Overview
Client → Controller → Service → ApiClient → Mock Server
↓               ↓
Validation      Downstream Validation Extraction
↓
Global Error Handler

👤 Author

Yogendra Singh Bundela
(Employee API Coding Challenge Solution)

✅ Summary

This solution implements a clean, production-ready REST API with:

Strong validation

Robust error handling

Full testing coverage

Downstream error sanitization

Extensible architecture