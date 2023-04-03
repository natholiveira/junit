package com.dojo.junit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private ApiError type;
    private String Any;
}
