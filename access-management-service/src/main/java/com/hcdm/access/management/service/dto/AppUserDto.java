package com.hcdm.access.management.service.dto;

import com.hcdm.access.management.service.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AppUserDto {
    private UUID id;
    @NotBlank
    private String username;
    @NotNull
    private UserRole role;
    private Boolean enabled;
}