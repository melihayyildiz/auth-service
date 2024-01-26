package com.ayyildizbank.userservice.service;

import com.ayyildizbank.userservice.auth.config.JwtUtils;
import com.ayyildizbank.userservice.auth.config.UserDetailsImpl;
import com.ayyildizbank.userservice.auth.model.Role;
import com.ayyildizbank.userservice.auth.model.RoleName;
import com.ayyildizbank.userservice.auth.model.User;
import com.ayyildizbank.userservice.payload.request.LoginRequest;
import com.ayyildizbank.userservice.payload.request.SignUpRequest;
import com.ayyildizbank.userservice.payload.response.LoginResponse;
import com.ayyildizbank.userservice.repository.RoleRepository;
import com.ayyildizbank.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtUtils jwtUtils;

    @InjectMocks
    AuthService authService;


    @Test
    void it_should_login_with_credentials(){

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("melih");
        loginRequest.setUsername("melih123");
        List<GrantedAuthority> authorities =  List.of(new SimpleGrantedAuthority(RoleName.ROLE_BUYER.name()));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "melih", "melih", "ayyildiz", "melih2@gmail.com", "$2a$10$GdxCf9sLFz7/9weIpll3/.chyUdAWY94QqzkhJgkqtKxXM.EbWl9.", authorities );

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        UsernamePasswordAuthenticationToken generated = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        Mockito.when(authenticationManager.authenticate(token)).thenReturn(generated);
        Mockito.when(jwtUtils.generateJwt(userDetails)).thenReturn("JWT123==");

        LoginResponse login = authService.login(loginRequest);
        assertEquals("JWT123==", login.getToken());

    }

    @Test
    void it_should_signup_with_user_info(){
        SignUpRequest signUpRequest = generateRequest();

        Mockito.when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        Mockito.when(roleRepository.existsById(signUpRequest.getRoleId())).thenReturn(true);
        Mockito.when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("!123??--llkqqrr");

        User user = User.builder().
            id(null)
            .username(signUpRequest.getUsername())
            .firstName(signUpRequest.getFirstName())
            .lastName(signUpRequest.getLastName())
            .email(signUpRequest.getEmail())
            .roles(new HashSet<>(Collections.singletonList(Role.builder().id(signUpRequest.getRoleId()).build())))
            .password("!123??--llkqqrr").build();

        authService.signUp(signUpRequest);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());

        assertEquals(user.getUsername(), userArgumentCaptor.getValue().getUsername());
        assertEquals(user.getFirstName(), userArgumentCaptor.getValue().getFirstName());
        assertEquals(user.getLastName(), userArgumentCaptor.getValue().getLastName());
        assertEquals(user.getEmail(), userArgumentCaptor.getValue().getEmail());
        assertEquals(user.getPassword(), userArgumentCaptor.getValue().getPassword());
        assertEquals(user.getRoles().size(), userArgumentCaptor.getValue().getRoles().size());

    }

    @Test
    void it_should_not_signup_with_existing_username(){
        SignUpRequest signUpRequest = generateRequest();

        Mockito.when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.signUp(signUpRequest);
        });

        String expectedMessage = "Same username exists in the system";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());
    }


    @Test
    void it_should_not_signup_with_existing_email(){
        SignUpRequest signUpRequest = generateRequest();
        Mockito.when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.signUp(signUpRequest);
        });

        String expectedMessage = "Same email exists in the system";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());
    }

    @Test
    void it_should_not_signup_with_invalid_role(){
        SignUpRequest signUpRequest = generateRequest();
        Mockito.when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        Mockito.when(roleRepository.existsById(signUpRequest.getRoleId())).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.signUp(signUpRequest);
        });

        String expectedMessage = "Role does not exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());
    }

    private SignUpRequest generateRequest(){
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("melih@melih.com");
        signUpRequest.setPassword("melih123");
        signUpRequest.setFirstName("melih");
        signUpRequest.setLastName("ayyildiz");
        signUpRequest.setRoleId(1L);
        signUpRequest.setUsername("mayyildiz");
        return signUpRequest;
    }
}