package com.reliaquest.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseModel<T> {
    private T data;
    private String statusCode;

    public ApiResponseModel() {}

    public ApiResponseModel(T data, String statusCode) {
        this.data = data;
        this.statusCode = statusCode;
    }
}
