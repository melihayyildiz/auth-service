package com.ayyildizbank.userservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorEnum implements ErrorResponse{
    USERNAME_EXISTS( "USERNAME_EXISTS", HttpStatus.BAD_REQUEST, "Same username exists in the system"),
    EMAIL_EXISTS( "EMAIL_EXISTS",HttpStatus.BAD_REQUEST, "Same email exists in the system"),
    ROLE_DOES_NOT_EXIST( "ROLE_DOES_NOT_EXIST",HttpStatus.BAD_REQUEST, "Role does not exist");

    final String key;
    final HttpStatus httpStatus;

    final String defaultMessage;

    ErrorEnum(String key, HttpStatus httpStatus, String defaultMessage) {
        this.key = key;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
