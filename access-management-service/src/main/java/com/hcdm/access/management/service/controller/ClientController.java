package com.hcdm.access.management.service.controller;

import com.hcdm.access.management.service.dto.ClientDto;
import com.hcdm.access.management.service.dto.CreateClientRequest;
import com.hcdm.access.management.service.entity.ClientStatus;
import com.hcdm.access.management.service.service.ClientService;
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
@RequestMapping("/clients")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Client Management", description = "Endpoints for managing clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    @Operation(summary = "Register a Client")
    public ResponseEntity<ClientDto> create(@Valid @RequestBody CreateClientRequest request) {
        return ResponseEntity.ok(clientService.createClient(request));
    }

    @GetMapping
    @Operation(summary = "List All registered Clients")
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Client By ID")
    public ResponseEntity<ClientDto> getClientById(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Client By ID")
    public ResponseEntity<ClientDto> updateClient(@PathVariable UUID id,
                                                  @Valid @RequestBody CreateClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Get Status of the Client By ID")
    public ResponseEntity<ClientDto> updateClientStatus(@PathVariable UUID id,
                                                        @RequestParam ClientStatus status) {
        return ResponseEntity.ok(clientService.updateClientStatus(id, status));
    }
}
