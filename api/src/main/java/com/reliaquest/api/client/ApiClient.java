package com.reliaquest.api.client;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import java.util.List;

public interface ApiClient {
    List<Employee> getAllEmployees();

    Employee getEmployeeById(String id);

    Employee createEmployee(CreateEmployeeRequest employee);

    boolean deleteEmployeeByName(String name);
}
