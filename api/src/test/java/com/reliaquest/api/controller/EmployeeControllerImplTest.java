package com.reliaquest.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeControllerImpl.class)
class EmployeeControllerImplTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;
    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
    // -------------------------------
    // NAME validation
    // -------------------------------
    @DisplayName("Name should not be too short")
    @Test
    void nameTooShort_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("ab", 100, 30, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("name must be at least 3 characters")));

        verifyNoInteractions(employeeService);
    }

    @DisplayName("Name should not be blank")
    @Test
    void nameBlank_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("", 100, 30, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Name must not be blank")));

        verifyNoInteractions(employeeService);
    }
    @DisplayName("Name should not be null")
    @Test
    void nameNull_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest(null, 100, 30, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Name must not be blank")));

        verifyNoInteractions(employeeService);
    }
    @DisplayName("Name should not contain any whitespace")
    @Test
    void nameWhitespace_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("   ", 100, 30, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Name must not be blank")));

        verifyNoInteractions(employeeService);
    }

    // -------------------------------
    // TITLE validation
    // -------------------------------
    @DisplayName("Employee title should not be blank")
    @Test
    void titleBlank_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", 100, 30, "");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("title must not be blank")));

        verifyNoInteractions(employeeService);
    }
    @DisplayName("Employee title should not be null")
    @Test
    void titleNull_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", 100, 30, null);

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("title must not be blank")));

        verifyNoInteractions(employeeService);
    }

    @DisplayName("Employee title should not contain any whitespace")
    @Test
    void titleWhitespace_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", 100, 30, "   ");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("title must not be blank")));

        verifyNoInteractions(employeeService);
    }

    // -------------------------------
    // SALARY validation
    // -------------------------------
    @DisplayName("Employee salary should not be null")
    @Test
    void salaryNull_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", null, 30, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Salary must not be null")));

        verifyNoInteractions(employeeService);
    }
    @DisplayName("Employee salary should not be below 0")
    @Test
    void salaryBelowMinimum_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", 0, 30, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("salary must be greater than 0")));

        verifyNoInteractions(employeeService);
    }

    // -------------------------------
    // AGE validation
    // -------------------------------
    @DisplayName("Employee age should not be null")
    @Test
    void ageNull_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", 100, null, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Age must not be null")));

        verifyNoInteractions(employeeService);
    }
    @DisplayName("Employee age should not be below 16")
    @Test
    void ageBelowMinimum_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", 100, 10, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("age must be at least 16")));

        verifyNoInteractions(employeeService);
    }
    @DisplayName("Employee age should not more then 75")
    @Test
    void ageAboveMaximum_shouldReturn400() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest("Alice", 100, 90, "Dev");

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("age must be at most 75")));

        verifyNoInteractions(employeeService);
    }
    @DisplayName("Fetch list of all employees")
    @Test
    void getAllEmployees_returnsOkAndList() throws Exception {
        Employee e1 = new Employee();
        e1.setId("1");
        e1.setName("Alice");
        Mockito.when(employeeService.getAllEmployees()).thenReturn(List.of(e1));
        mockMvc.perform(get("/api/v2/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].employee_name").value("Alice"));
    }
    @DisplayName("Search Employee by name")
    @Test
    void getEmployeesByNameSearch_bindsPathVariable() throws Exception {
        Employee e = new Employee();
        e.setId("1");
        e.setName("YOGI");
        Mockito.when(employeeService.getEmployeesByName("YOGI")).thenReturn(List.of(e));
        mockMvc.perform(get("/api/v2/employee/search/YOGI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employee_name").value("YOGI"));
        verify(employeeService).getEmployeesByName("YOGI");
    }
    @DisplayName("Get employee by ID")
    @Test
    void getEmployeeById_returnsEmployee() throws Exception {
        Employee e = new Employee();
        e.setId("123");
        e.setName("Bob");
        Mockito.when(employeeService.getEmployeeById("123")).thenReturn(e);
        mockMvc.perform(get("/api/v2/employee/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.employee_name").value("Bob"));
    }
    @DisplayName("Get Highest Salary of employee")
    @Test
    void getHighestSalaryOfEmployees_returnsOk_WithInteger() throws Exception {
        Mockito.when(employeeService.getHighestSalaryOfEmployees()).thenReturn(99999);

        mockMvc.perform(get("/api/v2/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("99999"));
    }
    @DisplayName("Get top 10 highest earning employee names")
    @Test
    void getTopTenHighestEarningEmployeeNames_returnsListOfStrings() throws Exception {
        Mockito.when(employeeService.getTop10HighestEarningEmployeeNames()).thenReturn(List.of("A", "B"));

        mockMvc.perform(get("/api/v2/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("A"))
                .andExpect(jsonPath("$[1]").value("B"));
    }
    @DisplayName("Create employee with valid data")
    @Test
    void createEmployee_bindsRequestBody_andReturnsCreated() throws Exception {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("Yogi", 65_000, 30, "Developer");
        Employee employeeCreated = new Employee();
        employeeCreated.setId("id-1");
        employeeCreated.setName("Yogi");
        Mockito.when(employeeService.createEmployee(any(CreateEmployeeRequest.class)))
                .thenReturn(employeeCreated);
        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.employee_name").value("Yogi"));
        verify(employeeService).createEmployee(any(CreateEmployeeRequest.class));
    }
    @DisplayName("Delete employee by ID")
    @Test
    void deleteEmployeeById_callsService_andReturnsName() throws Exception {
        Mockito.when(employeeService.deleteEmployeeById("YOGI")).thenReturn("YOGI");
        mockMvc.perform(delete("/api/v2/employee/YOGI"))
                .andExpect(status().isOk())
                .andExpect(content().string("YOGI"));
        verify(employeeService).deleteEmployeeById("YOGI");
    }

    // -------------------------------
    // MULTIPLE ERRORS in one request
    // -------------------------------
    @DisplayName("Throw 400 error with all messages when multiple fields has invalid data")
    @Test
    void multipleFieldsInvalid_shouldReturn400WithAllMessages() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest(
                "",      // invalid name (blank)
                0,       // invalid salary
                10,      // invalid age
                ""       // invalid title
        );
        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Name must not be blank")))
                .andExpect(jsonPath("$.message", containsString("salary must be greater than 0")))
                .andExpect(jsonPath("$.message", containsString("age must be at least 16")))
                .andExpect(jsonPath("$.message", containsString("title must not be blank")));

        verifyNoInteractions(employeeService);
    }

    // -------------------------------
    // EMPTY / NULL PAYLOAD
    // -------------------------------
    @DisplayName("Throw exception when Payload is empty")
    @Test
    void emptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest()); // HttpMessageNotReadableException

        verifyNoInteractions(employeeService);
    }

    // -------------------------------
    // HAPPY PATH: VALID REQUEST
    // -------------------------------
    @DisplayName("Create employee when we have valid request")
    @Test
    void validRequest_shouldReturn201AndCallService() throws Exception {
        CreateEmployeeRequest req = new CreateEmployeeRequest(
                "Alice",
                165000,
                38,
                "Senior Engineer"
        );

        Employee created = new Employee();
        created.setId("123");
        created.setName("Alice");
        created.setSalary(165000);
        created.setAge(38);
        created.setTitle("Senior Engineer");
        created.setEmail("alice@company.com");

        Mockito.when(employeeService.createEmployee(any(CreateEmployeeRequest.class)))
                .thenReturn(created);

        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.employee_name").value("Alice"))
                .andExpect(jsonPath("$.employee_salary").value(165000))
                .andExpect(jsonPath("$.employee_age").value(38))
                .andExpect(jsonPath("$.employee_title").value("Senior Engineer"));

        verify(employeeService).createEmployee(any(CreateEmployeeRequest.class));
    }
}
