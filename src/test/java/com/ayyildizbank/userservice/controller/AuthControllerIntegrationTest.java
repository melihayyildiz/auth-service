package com.ayyildizbank.userservice.controller;

import com.ayyildizbank.userservice.UserServiceApplication;
import com.ayyildizbank.userservice.payload.request.LoginRequest;
import com.ayyildizbank.userservice.payload.request.SignUpRequest;
import com.ayyildizbank.userservice.payload.response.CustomResponse;
import com.ayyildizbank.userservice.payload.response.SignupResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest {
    @LocalServerPort
    private int port;

    final TestRestTemplate restTemplate = new TestRestTemplate();


    @Test
    @Order(1)
    void testSignup() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.melih.api.v1+json");
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("melih@melih.com");
        signUpRequest.setPassword("melih123");
        signUpRequest.setFirstName("melih");
        signUpRequest.setLastName("ayyildiz");
        signUpRequest.setRoleId(1L);
        signUpRequest.setUsername("mayyildiz");


        HttpEntity<SignUpRequest> entity = new HttpEntity<>(signUpRequest, headers);

        ResponseEntity<CustomResponse> response = restTemplate.exchange(
            createURLWithPort("/api/auth/signup"),
            HttpMethod.POST, entity, CustomResponse.class);

        CustomResponse customResponse = response.getBody();

        Assertions.assertTrue(customResponse.isSuccess());
    }

    @Test
    @Order(2)
    void testLoginAfterSignup() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.melih.api.v1+json");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("mayyildiz");
        loginRequest.setPassword("melih123");

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<CustomResponse> response = restTemplate.exchange(
            createURLWithPort("/api/auth/login"),
            HttpMethod.POST, entity, CustomResponse.class);

        ObjectMapper mapper = new ObjectMapper();

        SignupResponse signupResponse = mapper.convertValue(response.getBody().getData(), new TypeReference<SignupResponse>() { });

        Assertions.assertTrue(response.getBody().isSuccess());
        Assertions.assertFalse(signupResponse.getToken().isBlank());

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}