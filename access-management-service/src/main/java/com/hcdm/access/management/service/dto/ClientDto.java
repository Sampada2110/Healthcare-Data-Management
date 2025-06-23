package com.hcdm.access.management.service.dto;

import com.hcdm.access.management.service.entity.ClientStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private UUID id;

    @NotBlank(message = "Client name is required")
    private String name;

    private ClientStatus status;
    private LocalDateTime createdAt;
}
