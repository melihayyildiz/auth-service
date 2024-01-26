package com.ayyildizbank.userservice.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends DefaultErrorAttributes {

    private final MessageSource messageSource;


    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handle(ApplicationException ex, WebRequest request) {
        return ofType(request, ex.getErrorResponse().getHttpStatus(), ex);
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    ResponseEntity<Map<String, Object>> handleAuthenticationException(Exception ex, WebRequest request) {
        Locale locale = LocaleUtils.toLocale(request.getHeader("locale"));
        String error = "USERNAME_PASS_WRONG";
        String msg = messageSource.getMessage(error, null, ex.getMessage(), Objects.isNull(locale) ? Locale.ENGLISH : locale );
        return ofType(request, HttpStatus.UNAUTHORIZED, msg, null, null);
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Locale locale = LocaleUtils.toLocale(request.getHeader("locale"));
        String error = "INTERNAL_SERVER_ERROR";
        String msg = messageSource.getMessage(error, null, ex.getMessage(), Objects.isNull(locale) ? Locale.ENGLISH : locale );
        return ofType(request, HttpStatus.INTERNAL_SERVER_ERROR, msg, null, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Map<String, Object>> handle(MethodArgumentNotValidException ex, WebRequest request) {
        List<ConstraintsViolationError> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ConstraintsViolationError(error.getField(), error.getDefaultMessage()))
            .toList();
        Locale locale = LocaleUtils.toLocale(request.getHeader("locale"));
        String error = "ARGUMENT_VALIDATION_ERROR";
        String msg = messageSource.getMessage(error, null, ex.getMessage(), Objects.isNull(locale) ? Locale.ENGLISH : locale );
        return ofType(request, HttpStatus.BAD_REQUEST, msg, null, validationErrors);
    }

    protected ResponseEntity<Map<String, Object>> ofType(WebRequest request, HttpStatus status, ApplicationException ex) {
        Locale locale = LocaleUtils.toLocale(request.getHeader("locale"));

        Object[] objects = ex.getMessageArguments().values().toArray();
        String msg = messageSource.getMessage(ex.getErrorResponse().getKey(), objects, ex.getMessage(), Objects.isNull(locale) ? Locale.ENGLISH : locale );

        return ofType(request, status, (msg == null || msg.isBlank()) ? ex.getMessage() : msg,
            ex.getErrorResponse().getKey(), Collections.emptyList());
    }

    private ResponseEntity<Map<String, Object>> ofType(WebRequest request, HttpStatus status, String message,
                                                       String key, List validationErrors) {
        Map<String, Object> attributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        attributes.put(HttpResponseConstants.STATUS, status.value());
        attributes.put(HttpResponseConstants.SUCCESS, false);
        attributes.put(HttpResponseConstants.ERROR, status);
        attributes.put(HttpResponseConstants.MESSAGE, message);
        attributes.put(HttpResponseConstants.ERRORS, validationErrors);
        attributes.put(HttpResponseConstants.ERROR_KEY, key);
        attributes.put(HttpResponseConstants.PATH, ((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(attributes, status);
    }
}

