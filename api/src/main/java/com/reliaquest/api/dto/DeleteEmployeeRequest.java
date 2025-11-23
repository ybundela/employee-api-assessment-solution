package com.reliaquest.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteEmployeeRequest {
    private String name;

    public DeleteEmployeeRequest() {}

    public DeleteEmployeeRequest(final String name) {
        this.name = name;
    }
}
