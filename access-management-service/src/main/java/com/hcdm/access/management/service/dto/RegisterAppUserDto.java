package com.hcdm.access.management.service.dto;

import com.hcdm.access.management.service.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterAppUserDto {
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 100)
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    private String password;
    @NotBlank(message = "Role must not be empty")
    private UserRole role;

}
