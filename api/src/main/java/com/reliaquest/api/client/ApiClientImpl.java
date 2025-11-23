package com.reliaquest.api.client;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.DeleteEmployeeRequest;
import com.reliaquest.api.model.ApiResponseModel;
import com.reliaquest.api.model.Employee;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiClientImpl implements ApiClient {

    private static final Logger log = LoggerFactory.getLogger(ApiClientImpl.class);
    private static final String CB_INSTANCE = "employeeApi";
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ApiClientImpl(
            RestTemplate restTemplate,
            @Value("${employee.mock.base-url:http://localhost:8112/api/v1/employee}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.debug("Calling Mock Employee API GET {}", baseUrl);

        ResponseEntity<ApiResponseModel<Employee[]>> response = restTemplate.exchange(
                baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<ApiResponseModel<Employee[]>>() {});

        ApiResponseModel<Employee[]> responseModel = response.getBody();
        Employee[] listOfEmployee = responseModel != null ? responseModel.getData() : null;
        return listOfEmployee == null ? List.of() : Arrays.asList(listOfEmployee);
    }

    @Override
    public Employee getEmployeeById(String id) {
        String url = baseUrl + "/" + id;
        log.debug("Calling Mock Employee API GET {}", url);
        try {
            ResponseEntity<ApiResponseModel<Employee>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<ApiResponseModel<Employee>>() {});
            ApiResponseModel<Employee> responseModel = response.getBody();
            return responseModel != null ? responseModel.getData() : null;
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Employee with id {} not found", id);
            return null;
        }
    }

    @Override
    public Employee createEmployee(CreateEmployeeRequest request) {
        log.debug("Calling Mock Employee API POST {} body={}", baseUrl, request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateEmployeeRequest> requestEntity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<ApiResponseModel<Employee>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponseModel<Employee>>() {});
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (HttpServerErrorException ex) {
            String body = ex.getResponseBodyAsString();
            log.warn("Downstream server/validation error: status={}, body={}", ex.getStatusCode(), body);
            String cleanedMessage = extractValidationMessage(body);
            throw new IllegalArgumentException(cleanedMessage);
        }
    }

    private String extractValidationMessage(String body) {
        if (body == null) return "Invalid employee data";
        // Regex to match: default message [some text]
        Pattern pattern = Pattern.compile("default message \\[(.*?)\\]");
        Matcher matcher = pattern.matcher(body);
        List<String> messages = new ArrayList<>();

        while (matcher.find()) {
            messages.add(matcher.group(1));
        }
        if (messages.isEmpty()) {
            return "Invalid employee data";
        }
        return String.join("; ", messages);
    }

    @Override
    public boolean deleteEmployeeByName(String name) {
        String url = baseUrl;
        log.debug("Calling Mock Employee API DELETE {} with name={}", url, name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        DeleteEmployeeRequest requestBody = new DeleteEmployeeRequest(name);
        HttpEntity<DeleteEmployeeRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ApiResponseModel<Boolean>> response = restTemplate.exchange(
                url, HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<ApiResponseModel<Boolean>>() {});

        ApiResponseModel<Boolean> responseBody = response.getBody();
        return responseBody != null && Boolean.TRUE.equals(responseBody.getData());
    }
}
