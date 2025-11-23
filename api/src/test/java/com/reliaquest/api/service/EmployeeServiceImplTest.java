package com.reliaquest.api.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmployeeServiceImplTest {
    private EmployeeServiceImpl employeeService;
    private ApiClient apiClient;

    @BeforeEach
    public void setUp() {
        apiClient = mock(ApiClient.class);
        employeeService = new EmployeeServiceImpl(apiClient);
    }

    @Test
    void getAllEmployees_returnsAllFromClient() {
        Employee e1 = employee("1", "YOGI", 400_000);
        Employee e2 = employee("2", "NIXON", 70_000);
        when(apiClient.getAllEmployees()).thenReturn(List.of(e1, e2));
        List<Employee> result = employeeService.getAllEmployees();
        assertThat(result).containsExactly(e1, e2);
    }

    @Test
    void getEmployeesByNameSearch_isCaseInsensitive_andUsesContains() {
        Employee e1 = employee("1", "Tiger Nixon", 100_000);
        Employee e2 = employee("2", "Bill Bob", 120_000);
        Employee e3 = employee("3", "Another Person", 90_000);
        when(apiClient.getAllEmployees()).thenReturn(List.of(e1, e2, e3));
        List<Employee> result = employeeService.getEmployeesByName("bob");
        assertThat(result).extracting(Employee::getName).containsExactly("Bill Bob");
    }

    @Test
    void getEmployeesByNameSearch_returnsEmptyListWhenNoMatches() {
        Employee e1 = employee("1", "Tiger Nixon", 100_000);
        when(apiClient.getAllEmployees()).thenReturn(List.of(e1));
        List<Employee> result = employeeService.getEmployeesByName("zzz");
        assertThat(result).isEmpty();
    }

    @Test
    void getEmployeeById_returnsEmployeeWhenFound() {
        Employee e = employee("123", "Alice", 100_000);
        when(apiClient.getEmployeeById("123")).thenReturn(e);
        Employee result = employeeService.getEmployeeById("123");
        assertThat(result.getId()).isEqualTo("123");
        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    void getEmployeeById_throwsWhenNotFound() {
        when(apiClient.getEmployeeById("missing")).thenReturn(null);
        assertThatThrownBy(() -> employeeService.getEmployeeById("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void getHighestSalaryOfEmployees_returnsMaxSalary_whenSomeHaveNullSalary() {
        Employee e1 = employee("1", "A", null);
        Employee e2 = employee("2", "B", 50_000);
        Employee e3 = employee("3", "C", 75_000);
        when(apiClient.getAllEmployees()).thenReturn(List.of(e1, e2, e3));
        Integer result = employeeService.getHighestSalaryOfEmployees();
        assertThat(result).isEqualTo(75_000);
    }

    @Test
    void getHighestSalaryOfEmployees_returnsZeroWhenNoEmployees() {
        when(apiClient.getAllEmployees()).thenReturn(List.of());
        Integer result = employeeService.getHighestSalaryOfEmployees();
        assertThat(result).isEqualTo(0);
    }

    @Test
    void getTop10HighestEarningEmployeeNames_returnsSortedNamesLimitedTo10() {
        List<Employee> employees = java.util.stream.IntStream.rangeClosed(1, 12)
                .mapToObj(i -> employee(String.valueOf(i), "Emp" + i, i * 1000))
                .collect(Collectors.toList());
        when(apiClient.getAllEmployees()).thenReturn(employees);

        List<String> result = employeeService.getTop10HighestEarningEmployeeNames();
        assertThat(result).hasSize(10);
        // highest salary first
        assertThat(result.get(0)).isEqualTo("Emp12");
        // 3rd lowest of full list is Emp3, so last of top10
        assertThat(result.get(9)).isEqualTo("Emp3");
    }

    @Test
    void createEmployee_delegatesToClient() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("John", 60_000, 30, "Dev");
        Employee created = employee("123", "John", 60_000);
        when(apiClient.createEmployee(request)).thenReturn(created);
        Employee result = employeeService.createEmployee(request);
        assertThat(result).isEqualTo(created);
        verify(apiClient).createEmployee(request);
    }

    @Test
    void deleteEmployeeById_returnsNameWhenClientReturnsTrue() {
        when(apiClient.deleteEmployeeByName("YOGI")).thenReturn(true);
        String result = employeeService.deleteEmployeeById("YOGI");
        assertThat(result).isEqualTo("YOGI");
    }

    @Test
    void deleteEmployeeById_throwsWhenClientReturnsFalse() {
        when(apiClient.deleteEmployeeByName("YOGI")).thenReturn(false);
        assertThatThrownBy(() -> employeeService.deleteEmployeeById("YOGI"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("YOGI");
    }

    private Employee employee(String id, String name, Integer salary) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setSalary(salary);
        return employee;
    }
}
