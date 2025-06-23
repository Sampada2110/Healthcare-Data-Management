package com.hcdm.access.management.service;

import com.hcdm.access.management.service.dto.*;
import com.hcdm.access.management.service.entity.AppUser;
import com.hcdm.access.management.service.entity.UserRole;
import com.hcdm.access.management.service.repository.UserRepository;
import com.hcdm.access.management.service.service.impl.UserServiceImpl;
import com.hcdm.access.management.service.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private Cache cache;

    @InjectMocks
    private UserServiceImpl userService;

    private AppUser adminUser;
    private AppUser testUser;
    private UUID clientId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        userId = UUID.randomUUID();

        adminUser = AppUser.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .role(UserRole.ADMIN)
                .client(new com.hcdm.access.management.service.entity.Client(clientId, "TestClient", null, null, null))
                .enabled(true)
                .build();

        testUser = AppUser.builder()
                .id(userId)
                .username("testuser")
                .password("encoded-pass")
                .role(UserRole.USER)
                .client(adminUser.getClient())
                .enabled(true)
                .build();
    }

    @Test
    void authenticateAndGenerateToken_Success() {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(testUser.getUsername(), clientId,testUser.getRole().name())).thenReturn("jwt-token");

        String token = userService.authenticateAndGenerateToken(request);

        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void registerUser_Success() {
        RegisterAppUserDto dto = new RegisterAppUserDto("newuser", "pass", UserRole.USER);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        userService.registerUser(dto, "admin");

        verify(userRepository).save(any(AppUser.class));
    }

    @Test
    void getUsersForClient_Success() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findByClientId(clientId)).thenReturn(List.of(testUser));

        List<AppUserDto> users = userService.getUsersForClient("admin");

        assertEquals(1, users.size());
        assertEquals("testuser", users.getFirst().getUsername());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findByIdAndClientId(userId, clientId)).thenReturn(Optional.of(testUser));
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        ResponseEntity<Void> response = userService.deleteUser(userId, "admin");

        assertEquals(204, response.getStatusCode().value());
        verify(userRepository).delete(testUser);
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findByIdAndClientId(userId, clientId)).thenReturn(Optional.of(testUser));

        AppUserDto dto = userService.getUserById(userId, "admin");

        assertEquals("testuser", dto.getUsername());
    }

    @Test
    void updateUser_Success() {
        UpdateAppUserDto dto = new UpdateAppUserDto(userId, true,UserRole.ADMIN);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findByIdAndClientId(userId, clientId)).thenReturn(Optional.of(testUser));
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        ResponseEntity<AppUserDto> response = userService.updateUser(dto, "admin");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(UserRole.ADMIN, Objects.requireNonNull(response.getBody()).getRole());
    }

    @Test
    void authenticateAndGenerateToken_UserNotFound_ThrowsException() {
        AuthenticationRequest request = new AuthenticationRequest("unknown", "pwd");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.authenticateAndGenerateToken(request));
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findByIdAndClientId(userId, clientId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(userId, "admin"));
    }
}
