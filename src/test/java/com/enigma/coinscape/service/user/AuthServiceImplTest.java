package com.enigma.coinscape.service.user;

import com.enigma.coinscape.entities.Role;
import com.enigma.coinscape.entities.User;
import com.enigma.coinscape.entities.UserCredential;
import com.enigma.coinscape.entities.UserDetailImpl;
import com.enigma.coinscape.entities.constants.ERole;
import com.enigma.coinscape.models.requests.auth.AuthRequest;
import com.enigma.coinscape.models.responses.auth.LoginResponse;
import com.enigma.coinscape.models.responses.auth.RegisterResponse;
import com.enigma.coinscape.repository.AdminRepository;
import com.enigma.coinscape.repository.UserCredentialRepository;
import com.enigma.coinscape.repository.UserRepository;
import com.enigma.coinscape.security.BCriptUtils;
import com.enigma.coinscape.security.JwtUtils;
import com.enigma.coinscape.service.AuthService;
import com.enigma.coinscape.service.RoleService;
import com.enigma.coinscape.service.UserService;
import com.enigma.coinscape.service.impl.AuthServiceImpl;
import com.enigma.coinscape.service.impl.UserServiceImpl;
import com.enigma.coinscape.utils.ValidationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest public class AuthServiceImplTest {
    private final JwtUtils jwtUtils = mock(JwtUtils.class);
    private final RoleService roleService = mock(RoleService.class);
    private final BCriptUtils bCriptUtils = mock(BCriptUtils.class);
    private final UserService userService = mock(UserServiceImpl.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ValidationUtil validationUtil = mock(ValidationUtil.class);
    private final Authentication authentication = mock(Authentication.class);
    private final AdminRepository adminRepository = mock(AdminRepository.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final UserCredentialRepository userCredentialRepository = mock(UserCredentialRepository.class);
    private final AuthService authService = new AuthServiceImpl(jwtUtils,bCriptUtils,roleService,
            userRepository,adminRepository,authenticationManager,userCredentialRepository);

    @BeforeEach
    void setUp() {
        reset(userCredentialRepository, userRepository, validationUtil, bCriptUtils,
                authenticationManager, adminRepository, jwtUtils,
                roleService, userService, authentication);
    }

    @Test
    void createNewUser() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("user1@gmail.com");
        authRequest.setPassword("password");
        authRequest.setName("user");
        authRequest.setMobilePhone("0850xxxx");

        Role role = new Role();
        role.setId("123");

        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(authRequest.getEmail());
        userCredential.setPassword("hashedPassword");
        userCredential.setRoles(List.of(role));

        User user1 = new User();
        user1.setId("123");
        user1.setEmail(authRequest.getEmail());
        user1.setName(authRequest.getName());
        user1.setPhoneNumber(authRequest.getMobilePhone());
        user1.setUserCredential(userCredential);

        when(roleService.getOrSave(any(ERole.class))).thenReturn(role);
        when(bCriptUtils.hashPassword(anyString())).thenReturn("hashedPassword");
        when(userCredentialRepository.saveAndFlush(any(UserCredential.class))).thenReturn(userCredential);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user1);

        RegisterResponse registerResponse = authService.registerUser(authRequest);

        assertNotNull(registerResponse);
        assertEquals(authRequest.getEmail(), registerResponse.getEmail());

        verify(roleService, times(1)).getOrSave(ERole.ROLE_REGULAR_USER);
        verify(bCriptUtils, times(1)).hashPassword(authRequest.getPassword());
        verify(userCredentialRepository, times(1)).saveAndFlush(any(UserCredential.class));
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void loginUser() {
        String email = "user@example.com";
        String password = "password";
        UserDetailImpl userDetail = new UserDetailImpl(email, password, Collections.emptyList());
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(email);
        authRequest.setPassword(password);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetail);

        User user = new User();
        user.setEmail(email);
        user.setIsPremium(true);
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.of(user));

        String token = "user-token";
        when(jwtUtils.generateToken(email)).thenReturn(token);

        LoginResponse response = authService.loginUser(authRequest);

        assertEquals(email, response.getEmail());
        Assertions.assertFalse(response.getRoles().contains(ERole.ROLE_PREMIUM_USER.name()));
        assertEquals(token, response.getToken());
    }
}
