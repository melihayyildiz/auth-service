package com.ayyildizbank.userservice.auth.config;

import com.ayyildizbank.userservice.auth.model.Role;
import com.ayyildizbank.userservice.auth.model.RoleName;
import com.ayyildizbank.userservice.auth.model.User;
import com.ayyildizbank.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;


    @Test
    void findUser_byUserName_whenLoadUserByUsername_isCalled(){

        String username = "melih";
        User user = new User();
        user.setUsername("melih");
        user.setId(1L);
        user.setEmail("melih@melih.com");
        user.setPassword("123123");
        user.setFirstName("Melih");
        user.setLastName("Ayyildiz");
        user.setRoles(Set.of(new Role(1L, RoleName.ROLE_BUYER)));

        List<GrantedAuthority> authorities =  List.of(new SimpleGrantedAuthority(RoleName.ROLE_BUYER.name()));
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        assertEquals( "123123", userDetails.getPassword());
        assertEquals("melih", userDetails.getUsername());
        assertEquals( "Ayyildiz", userDetails.getLastName());
        assertEquals("Melih", userDetails.getFirstName());
        assertEquals("melih@melih.com", userDetails.getEmail());
        assertEquals(1L, userDetails.getId());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(authorities.get(0)));
        verify(userRepository, Mockito.times(1)).findByUsername(username);
    }


    @Test
    void loadByUserName_shouldThrowUserNotFoundException_whenUserNameNotFound(){

        String username = "melih";
        User user = new User();
        user.setUsername("melih");
        user.setId(1L);
        user.setEmail("melih@melih.com");
        user.setPassword("123123");
        user.setFirstName("Melih");
        user.setLastName("Ayyildiz");
        user.setRoles(Set.of(new Role(1L, RoleName.ROLE_BUYER)));

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        String expectedMessage = "User Not Found with username: " + user.getUsername();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("UsernameNotFoundException", exception.getClass().getSimpleName());


    }
}