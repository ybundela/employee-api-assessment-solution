# Employee API – Coding Challenge Solution

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Build](https://img.shields.io/badge/Build-Gradle-success)
![Tests](https://img.shields.io/badge/Tests-Passing-brightgreen)
![Coverage](https://img.shields.io/badge/Coverage-90%25-green)

This project implements a Spring Boot **Employee API** exposing:
http://localhost:8111/api/v2/employee

It integrates with a downstream Mock Employee Server:

http://localhost:8112/api/v1/employee


This solution demonstrates:
- Clean architecture & coding principles
- REST API best practices
- Comprehensive validation
- Global exception handling
- Downstream error translation
- Full unit & integration testing

---

## 📂 Project Structure

```text
root/
├── api/                     
│   ├── controller/          
│   ├── service/             
│   ├── client/              
│   ├── dto/                 
│   ├── model/               
│   ├── error/               
│   ├── test/                
│   └── ApiApplication.java
└── server/ 



🚀 REST Endpoints
Employee API Endpoints

| Method | Endpoint                                                 | Description                          |
|--------|-----------------------------------------------------------|--------------------------------------|
| GET    | `/api/v2/employee`                                       | Get all employees                    |
| GET    | `/api/v2/employee/search/{name}`                         | Search employees by name fragment    |
| GET    | `/api/v2/employee/{id}`                                  | Get employee by ID                   |
| GET    | `/api/v2/employee/highestSalary`                         | Get highest employee salary          |
| GET    | `/api/v2/employee/topTenHighestEarningEmployeeNames`     | Get top 10 earning employee names    |
| POST   | `/api/v2/employee`                                       | Create a new employee                |
| DELETE | `/api/v2/employee/{name}`                                | Delete employee by name              |





🧱 Core Concepts Demonstrated
1. Input Validation (Jakarta Validation)


@NotBlank(message = "Name must not be blank")
@Size(min = 3, message = "name must be at least 3 characters")
private String name;

Controller:

public ResponseEntity<Employee> createEmployee(
        @Valid @RequestBody CreateEmployeeRequest request) {
}


Error response:

{
  "timestamp": "2025-11-23T14:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "name must be at least 3 characters",
  "path": "/api/v2/employee"
}

2. Global Exception Handling

Handled exceptions:

MethodArgumentNotValidException → 400

IllegalArgumentException → 400

HttpClientErrorException.TooManyRequests → 429

Consistent JSON error responses are returned.

3. Downstream Error Translation

Mock server may return verbose error blobs:

default message [must be greater than or equal to 16]


Client extracts the relevant message:

catch (HttpServerErrorException ex) {
    String cleaned = extractValidationMessage(ex.getResponseBodyAsString());
    throw new IllegalArgumentException(cleaned);
}


Cleaned API output:

{
  "message": "must be greater than or equal to 16"
}



4. Unit & Integration Testing

Test suite covers:
1) Controller validation
2) Service logic
3) API client behavior
4) Downstream error sanitation
5) Integration flows



5. Uses:

1) MockMvc
2) MockRestServiceServer


6. Spring Boot Test

▶️ Running the Application
1. Start Mock Server
cd server
./gradlew bootRun


7. Mock server URL:

http://localhost:8112/api/v1/employee

8. Start the Employee API
cd api
./gradlew bootRun


9) API URL:
http://localhost:8111/api/v2/employee


🧪 Running Tests
./gradlew test

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

application-test.yml
employee:
  mock:
    base-url: http://localhost:9999/api/v1/employee



📡 API Client

**Features:**
RestTemplate
Connection/read timeouts
JSON parsing
Downstream validation extraction
Clean error propagation

Example handling:

catch (HttpServerErrorException ex) {
    String cleaned = extractValidationMessage(ex.getResponseBodyAsString());
    throw new IllegalArgumentException(cleaned);
}


📈 Scalability & Resilience

Implemented

1) Local request validation
2) Downstream error translation
3) RestTemplate timeouts
4) Stateless service layer
5) Clean separation of layers


 **Future Enhancements**
1) Replace RestTemplate with WebClient

2) Add Resilience4j (CircuitBreaker, Retry)

3) Add caching for frequent read endpoints

4) Improve tracing & observability



🏗 Architecture Overview
Client
  ↓
Controller
  ↓
Service
  ↓
API Client
  ↓
Mock Employee Server



👤 Author

Yogendra Singh Bundela
Employee API – Coding Challenge Solution




✅ Summary

This solution provides:

Clean REST design

Strong validation

Robust global exception handling

Downstream error sanitization

Comprehensive unit + integration tests

Production-ready architecture

