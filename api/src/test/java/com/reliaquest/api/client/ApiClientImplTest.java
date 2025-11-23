package com.reliaquest.api.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.model.ApiResponseModel;
import com.reliaquest.api.model.Employee;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class ApiClientImplTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private ApiClientImpl client;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        client = new ApiClientImpl(restTemplate, "http://localhost:8112/api/v1/employee");
    }

    @Test
    void getAllEmployees_callsCorrectUrl_andDeserializes() throws Exception {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("Alice");
        employee.setSalary(100_000);
        ApiResponseModel<Employee[]> response = new ApiResponseModel<>(new Employee[] {employee}, "ok");

        server.expect(once(), requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        List<Employee> result = client.getAllEmployees();
        server.verify();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
    }

    @Test
    void getEmployeeById_callsCorrectUrl_andDeserializes() throws Exception {
        Employee e = new Employee();
        e.setId("123");
        e.setName("Bob");
        ApiResponseModel<Employee> response = new ApiResponseModel<>(e, "ok");
        server.expect(once(), requestTo("http://localhost:8112/api/v1/employee/123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
        Employee result = client.getEmployeeById("123");
        server.verify();
        assertThat(result.getId()).isEqualTo("123");
    }

    @Test
    void createEmployee_postsJsonAndDeserializesResponse() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest("John", 50_000, 25, "Dev");
        Employee created = new Employee();
        created.setId("id-1");
        created.setName("John");
        ApiResponseModel<Employee> response = new ApiResponseModel<>(created, "ok");
        server.expect(once(), requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
        Employee result = client.createEmployee(request);
        server.verify();
        assertThat(result.getId()).isEqualTo("id-1");
    }

    @Test
    void deleteEmployeeByName_sendsDeleteWithJsonBody_andReturnsTrue() throws Exception {
        ApiResponseModel<Boolean> response = new ApiResponseModel<>(true, "ok");
        server.expect(once(), requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"name\":\"YOGI\"}"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
        boolean deleted = client.deleteEmployeeByName("YOGI");
        server.verify();
        assertThat(deleted).isTrue();
    }
}
