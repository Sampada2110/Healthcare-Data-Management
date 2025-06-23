package com.hcdm.access.management.service.controller;


import com.hcdm.access.management.service.dto.AppUserDto;
import com.hcdm.access.management.service.dto.UpdateAppUserDto;
import com.hcdm.access.management.service.service.UserService;
import com.hcdm.access.management.service.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Application User Management", description = "Endpoints for managing application users")
public class AppUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List Users by client")
    public ResponseEntity<List<AppUserDto>> listUsersForClient(@RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromHeader(authHeader);
        List<AppUserDto> users = userService.getUsersForClient(username);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get User By ID")
    public ResponseEntity<AppUserDto> getUser(@PathVariable UUID id,
                                              @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromHeader(authHeader);
        AppUserDto user = userService.getUserById(id, username);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete User By ID")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id,
                                           @RequestHeader("Authorization") String authHeader) {
        String username = extractUsernameFromHeader(authHeader);
        return userService.deleteUser(id, username);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update User By ID")
    public ResponseEntity<AppUserDto> updateUser(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdateAppUserDto dto,
                                                 @RequestHeader("Authorization") String authHeader) {
        dto.setId(id);
        String username = extractUsernameFromHeader(authHeader);
        return userService.updateUser(dto, username);
    }

    private String extractUsernameFromHeader(String authHeader) {
        return jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
    }
}