package com.ayyildizbank.userservice.controller;

import com.ayyildizbank.userservice.payload.request.LoginRequest;
import com.ayyildizbank.userservice.payload.request.SignUpRequest;
import com.ayyildizbank.userservice.payload.response.CustomResponse;
import com.ayyildizbank.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", produces = "application/vnd.melih.api.v1+json")
    public ResponseEntity<CustomResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("AuthController.login login attempt for {}", loginRequest.getUsername());
        return ResponseEntity.ok(CustomResponse.success(authService.login(loginRequest)));
    }

    @PostMapping(value = "/signup", produces = "application/vnd.melih.api.v1+json")
    public ResponseEntity<CustomResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        log.info("AuthController.signUp signup attempt for {}", signUpRequest.getUsername());
        authService.signUp(signUpRequest);
        return ResponseEntity.ok(CustomResponse.success());
    }
}