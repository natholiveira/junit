package com.dojo.junit.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends Exception {

    ApiException(Throwable cause) { super(cause); }
    ApiException(String message) { super(message); }
    ApiException(Throwable cause, String message) { super(message, cause); }

    public abstract HttpStatus httpStatus();
    abstract ApiError apiError();
    abstract String userResponseMessage();

    public ErrorResponse createErrorResponse() {
        return new ErrorResponse(
                apiError(),
                userResponseMessage()
        );
    }
}
