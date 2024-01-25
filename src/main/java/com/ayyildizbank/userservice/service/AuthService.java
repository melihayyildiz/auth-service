package com.ayyildizbank.userservice.service;


import com.ayyildizbank.userservice.auth.config.JwtUtils;
import com.ayyildizbank.userservice.auth.config.UserDetailsImpl;
import com.ayyildizbank.userservice.auth.model.Role;
import com.ayyildizbank.userservice.auth.model.User;
import com.ayyildizbank.userservice.exception.ApplicationException;
import com.ayyildizbank.userservice.payload.request.LoginRequest;
import com.ayyildizbank.userservice.payload.request.SignUpRequest;
import com.ayyildizbank.userservice.payload.response.LoginResponse;
import com.ayyildizbank.userservice.repository.RoleRepository;
import com.ayyildizbank.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import static com.ayyildizbank.userservice.exception.ErrorEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    public LoginResponse login(LoginRequest loginRequest){

        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwt(userDetails);

        log.info("AuthService.login login successful for {}", loginRequest.getUsername());
        return new LoginResponse(jwt);
    }

    @Transactional
    public void signUp(SignUpRequest signUpRequest){
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new ApplicationException(USERNAME_EXISTS, Map.of("username", signUpRequest.getUsername()));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ApplicationException(EMAIL_EXISTS, Map.of("email", signUpRequest.getEmail()));
        }
        if (!roleRepository.existsById(signUpRequest.getRoleId())) {
            throw new ApplicationException(ROLE_DOES_NOT_EXIST, Map.of("id", signUpRequest.getRoleId()));
        }
        String password = passwordEncoder.encode(signUpRequest.getPassword());
        User user = User.builder().
                    id(null)
            .username(signUpRequest.getUsername())
            .firstName(signUpRequest.getFirstName())
            .lastName(signUpRequest.getLastName())
            .email(signUpRequest.getEmail())
            .roles(new HashSet<>(Collections.singletonList(Role.builder().id(signUpRequest.getRoleId()).build())))
            .password(password).build();

        userRepository.save(user);
        log.info("AuthService.signUp signUp is successful for {}", signUpRequest.getUsername());
    }
}
