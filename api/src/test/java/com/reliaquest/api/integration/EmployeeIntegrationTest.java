package com.reliaquest.api.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.model.ApiResponseModel;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class EmployeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate; // same bean used by ApiClientImpl

    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getAllEmployees_happyPath() throws Exception {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("Alice"); // or setName, depending on your model
        employee.setSalary(100_000); // or setSalary

        ApiResponseModel<Employee[]> response = new ApiResponseModel<>(new Employee[] {employee}, "ok");

        server.expect(once(), requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/v2/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].employee_name").value("Alice"));

        server.verify();
    }

    @Test
    void deleteEmployee_flowsThroughAllLayers() throws Exception {
        ApiResponseModel<Boolean> deleteResponse = new ApiResponseModel<>(true, "ok");
        server.expect(once(), requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(method(org.springframework.http.HttpMethod.DELETE))
                .andExpect(org.springframework.test.web.client.match.MockRestRequestMatchers.content()
                        .json("{\"name\":\"YOGI\"}"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(deleteResponse), MediaType.APPLICATION_JSON));

        mockMvc.perform(delete("/api/v2/employee/YOGI"))
                .andExpect(status().isOk())
                .andExpect(content().string("YOGI"));
        server.verify();
    }

    @Test
    void createEmployee_whenMockServerReturnsValidation500_returnsClean400WithMessage() throws Exception {
        // Arrange: valid request from our API's perspective
        CreateEmployeeRequest request = new CreateEmployeeRequest("Alice", 165000, 38, "Developer");
        String jsonBody = objectMapper.writeValueAsString(request);
        // This is the kind of big ugly error body the mock server returns
        String downstreamErrorJson =
                """
            {
              "status": "Failed to process request.",
              "error": "Validation failed for argument [0] in public com.myTest.server.model.Response<com.myTest.server.model.MockEmployee> com.myTest.server.controller.MockEmployeeController.createEmployee(com.myTest.server.model.CreateMockEmployeeInput): [Field error in object 'createMockEmployeeInput' on field 'age': rejected value [0]; codes [Min.createMockEmployeeInput.age,Min.age,Min.java.lang.Integer,Min]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [createMockEmployeeInput.age,age]; arguments []; default message [age],16]; default message [must be greater than or equal to 16]] "
            }
            """;
        // Mock the downstream POST call to return 500 with that body
        server.expect(once(), requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(downstreamErrorJson));

        // Act & Assert: call our API and expect 400 with a cleaned-up message
        mockMvc.perform(post("/api/v2/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                // This is the important part: only the default message, not the whole server blob
                .andExpect(jsonPath("$.message", containsString("must be greater than or equal to 16")));

        server.verify();
    }
}
