package com.reliaquest.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployeeRequest {
    @NotBlank(message = "Name must not be blank")
    @Size(min = 3, message = "name must be at least 3 characters")
    private String name;

    @NotNull(message = "Salary must not be null") @Min(value = 1, message = "salary must be greater than 0")
    private Integer salary;

    @NotNull(message = "Age must not be null") @Min(value = 16, message = "age must be at least 16")
    @Max(value = 75, message = "age must be at most 75")
    private Integer age;

    @NotBlank(message = "title must not be blank")
    private String title;

    public CreateEmployeeRequest() {}

    public CreateEmployeeRequest(final String name, final Integer salary, final Integer age, final String title) {
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.title = title;
    }
}
