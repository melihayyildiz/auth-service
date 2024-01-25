package com.ayyildizbank.userservice.controller;

import com.ayyildizbank.userservice.payload.request.LoginRequest;
import com.ayyildizbank.userservice.payload.request.SignUpRequest;
import com.ayyildizbank.userservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    AuthService authService;

    @InjectMocks
    AuthController authController;

    @Test
    void authController_shouldCall_authService_login_withLoginRequest(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("melih");
        loginRequest.setPassword("123456");

        ArgumentCaptor<LoginRequest> captor = ArgumentCaptor.forClass(LoginRequest.class);

        authController.login(loginRequest);

        Mockito.verify(authService, times(1)).login(captor.capture());

        assertEquals(loginRequest, captor.getValue());
    }

    @Test
    void authController_shouldCall_authService_signup_withSignupRequest(){
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("melih@melih.com");
        signUpRequest.setPassword("melih123");
        signUpRequest.setFirstName("melih");
        signUpRequest.setLastName("ayyildiz");
        signUpRequest.setRoleId(1L);
        signUpRequest.setUsername("mayyildiz");

        ArgumentCaptor<SignUpRequest> captor = ArgumentCaptor.forClass(SignUpRequest.class);

        authController.signUp(signUpRequest);

        Mockito.verify(authService, times(1)).signUp(captor.capture());

        assertEquals(signUpRequest, captor.getValue());
    }
}