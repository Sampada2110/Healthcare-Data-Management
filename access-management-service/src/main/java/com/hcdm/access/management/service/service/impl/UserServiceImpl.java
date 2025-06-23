package com.hcdm.access.management.service.service.impl;


import com.hcdm.access.management.service.dto.AppUserDto;
import com.hcdm.access.management.service.dto.AuthenticationRequest;
import com.hcdm.access.management.service.dto.RegisterAppUserDto;
import com.hcdm.access.management.service.dto.UpdateAppUserDto;
import com.hcdm.access.management.service.entity.AppUser;
import com.hcdm.access.management.service.repository.UserRepository;
import com.hcdm.access.management.service.service.UserService;
import com.hcdm.access.management.service.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String authenticateAndGenerateToken(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return jwtUtil.generateToken(user.getUsername(), user.getClient().getId(),user.getRole().name());
    }

    @CacheEvict(value = "usersByClient", key = "#adminUsername")
    @Override
    public void registerUser(RegisterAppUserDto dto, String adminUsername) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        AppUser admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        AppUser newUser = AppUser.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .enabled(true)
                .client(admin.getClient())
                .build();

        userRepository.save(newUser);
    }

    @Cacheable(value = "usersByClient", key = "#adminUsername")
    @Override
    @Transactional(readOnly = true)
    public List<AppUserDto> getUsersForClient(String adminUsername) {
        AppUser admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        return userRepository.findByClientId(admin.getClient().getId())
                .stream()
                .map(user -> AppUserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .enabled(user.getEnabled())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id, String adminUsername) {
        AppUser admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        Optional<AppUser> user = userRepository.findByIdAndClientId(id, admin.getClient().getId());
        if (user.isPresent()) {
            userRepository.delete(user.get());
            Objects.requireNonNull(cacheManager.getCache("userById")).evict(id);
            Objects.requireNonNull(cacheManager.getCache("usersByClient")).evict(adminUsername);
            Objects.requireNonNull(cacheManager.getCache("users")).evict(user.get().getUsername());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Cacheable(value = "userById", key = "#id")
    @Override
    @Transactional(readOnly = true)
    public AppUserDto getUserById(UUID id, String adminUsername) {
        AppUser admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        return userRepository.findByIdAndClientId(id, admin.getClient().getId())
                .map(user -> AppUserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .enabled(user.getEnabled())
                        .build())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    @Override
    public ResponseEntity<AppUserDto> updateUser(UpdateAppUserDto dto, String adminUsername) {
        AppUser admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        return userRepository.findByIdAndClientId(dto.getId(), admin.getClient().getId())
                .map(user -> {
                    user.setEnabled(dto.getEnabled());
                    user.setRole(dto.getRole());
                    userRepository.save(user);
                    Objects.requireNonNull(cacheManager.getCache("userById")).evict(user.getId());
                    Objects.requireNonNull(cacheManager.getCache("usersByClient")).evict(adminUsername);
                    Objects.requireNonNull(cacheManager.getCache("users")).evict(user.getUsername());

                    AppUserDto response = AppUserDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .enabled(user.getEnabled())
                            .role(user.getRole())
                            .build();

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
