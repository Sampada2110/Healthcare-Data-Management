package com.hcdm.access.management.service.service;


import com.hcdm.access.management.service.dto.AppUserDto;
import com.hcdm.access.management.service.dto.AuthenticationRequest;
import com.hcdm.access.management.service.dto.RegisterAppUserDto;
import com.hcdm.access.management.service.dto.UpdateAppUserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    String authenticateAndGenerateToken(AuthenticationRequest request);

    void registerUser(RegisterAppUserDto dto, String adminUsername);
    List<AppUserDto> getUsersForClient(String adminUsername);
    ResponseEntity<Void> deleteUser(UUID id, String adminUsername);
    AppUserDto getUserById(UUID id, String adminUsername);
    ResponseEntity<AppUserDto> updateUser(UpdateAppUserDto dto, String adminUsername);
}
