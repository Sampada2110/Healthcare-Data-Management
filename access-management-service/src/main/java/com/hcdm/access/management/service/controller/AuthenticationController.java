package com.hcdm.access.management.service.controller;


import com.hcdm.access.management.service.dto.AuthenticationRequest;
import com.hcdm.access.management.service.dto.RegisterAppUserDto;
import com.hcdm.access.management.service.service.UserService;
import com.hcdm.access.management.service.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthenticationRequest request) {
    //    try {
            log.info("Attempting login for user: {}", request.getUsername());

        final String jwt = userService.authenticateAndGenerateToken(request);
            return ResponseEntity.ok(Map.of("token", jwt));
//        } catch (Exception e) {
//            log.warn("Login failed for user: {}. Reason: {}", request.getUsername(), e.getMessage());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Invalid credentials"));
//        }

    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerUser( @Valid @RequestBody RegisterAppUserDto request,
                                               @RequestHeader("Authorization") String authHeader) {
//        try{
        String token = authHeader.replace("Bearer ", "");
        String adminUsername = jwtUtil.extractUsername(token);
        userService.registerUser(request, adminUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        }
//        catch (Exception e) {
//            log.warn("Register failed for user: {}. Reason: {}", request.getUsername(), e.getMessage());
//            return ResponseEntity.ok("AppUser not create successfully");}
//        }


}