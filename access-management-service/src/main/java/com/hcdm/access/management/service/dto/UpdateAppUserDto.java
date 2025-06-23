package com.hcdm.access.management.service.dto;

import com.hcdm.access.management.service.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateAppUserDto {

    @NotNull(message = "User ID is required")
    private UUID id;

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    @NotNull(message = "Role is required")
    private UserRole role;
}
