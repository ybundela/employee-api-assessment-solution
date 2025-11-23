package com.reliaquest.api.controller;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/employee")
public class EmployeeControllerImpl implements IEmployeeController<Employee, CreateEmployeeRequest> {
    private static final Logger log = LoggerFactory.getLogger(EmployeeControllerImpl.class);
    private final EmployeeService employeeService;

    public EmployeeControllerImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Get /api/v2/employee - all employees");
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("Get /api/v2/employee - search employees by name - getEmployeesByNameSearch- {}", searchString);
        List<Employee> employees = employeeService.getEmployeesByName(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        log.info("Get /api/v2/employee - getEmployeeById - {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Get /api/v2/employee - getHighestSalaryOfEmployees");
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Get /api/v2/employee - topTenHighestEarningEmployeeNames");
        List<String> name = employeeService.getTop10HighestEarningEmployeeNames();
        return ResponseEntity.ok(name);
    }

    @Override
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody CreateEmployeeRequest employeeRequest) {
        log.info("Create /api/v2/employee - {}", employeeRequest);
        Employee created = employeeService.createEmployee(employeeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String name) {
        log.info("Delete /api/v2/employee - {}", name);
        String deletedEmployeeName = employeeService.deleteEmployeeById(name);
        return ResponseEntity.ok(deletedEmployeeName);
    }
}
