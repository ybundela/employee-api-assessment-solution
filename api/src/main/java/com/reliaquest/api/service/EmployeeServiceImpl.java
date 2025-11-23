package com.reliaquest.api.service;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final ApiClient apiClient;

    public EmployeeServiceImpl(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.info("Getting all employees");
        return apiClient.getAllEmployees();
    }

    @Override
    public List<Employee> getEmployeesByName(String name) {
        log.info("Searching employees by name : {}", name);
        String fragment = Optional.ofNullable(name)
                .map(nameFragment -> nameFragment.toLowerCase(Locale.ROOT))
                .orElse("");
        return apiClient.getAllEmployees().stream()
                .filter(employeeName -> employeeName.getName() != null
                        && employeeName.getName().toLowerCase(Locale.ROOT).contains(fragment))
                .collect(Collectors.toList());
    }

    @Override
    public Employee getEmployeeById(String id) {
        log.info("Searching employee by id : {}", id);
        Employee employee = apiClient.getEmployeeById(id);
        if (employee == null) {
            log.warn("Employee with id {} not found", id);
            throw new IllegalArgumentException("Employee with id " + id + " not found");
        }
        return employee;
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        log.info("Searching highest salary of employees");
        return apiClient.getAllEmployees().stream()
                .map(Employee::getSalary)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public List<String> getTop10HighestEarningEmployeeNames() {
        log.info("Searching top 10 highest employee names");
        return apiClient.getAllEmployees().stream()
                .filter(employeeSalary -> employeeSalary.getSalary() != null)
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Employee createEmployee(CreateEmployeeRequest request) {
        log.info("Creating employee : {}", request.getName());
        return apiClient.createEmployee(request);
    }

    @Override
    public String deleteEmployeeById(String name) {
        log.info("Deleting employee by name : {}", name);
        boolean isDeleted = apiClient.deleteEmployeeByName(name);
        if (!isDeleted) {
            log.warn("Failed to delete employee id {}", name);
            throw new IllegalArgumentException("Unable to delete Employee with id " + name);
        }
        return name;
    }
}
