package com.dojo.junit.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedUserException extends ApiException {

    public DuplicatedUserException(String username) {
        super( "User with username "+username+" already exists" );
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    ApiError apiError() {
        return ApiError.USER_ALREADY_EXISTS;
    }

    @Override
    String userResponseMessage() {
        return getMessage();
    }
}

