package com.ayyildizbank.userservice.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;


    public AuthEntryPointJwt(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        log.error("AuthEntryPointJwt.commence Unauthorized error: {}", authException.getMessage());
        this.resolver.resolveException(request, response, null, authException);
    }

}
