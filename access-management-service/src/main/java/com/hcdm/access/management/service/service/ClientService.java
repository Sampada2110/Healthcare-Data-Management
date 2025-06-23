package com.hcdm.access.management.service.service;

import com.hcdm.access.management.service.dto.ClientDto;
import com.hcdm.access.management.service.dto.CreateClientRequest;
import com.hcdm.access.management.service.entity.ClientStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientDto createClient(CreateClientRequest request);
    List<ClientDto> getAllClients();

    ClientDto updateClient(UUID id, CreateClientRequest request);

    ClientDto updateClientStatus(UUID id, ClientStatus status);

    @Transactional(readOnly = true)
    ClientDto getClientById(UUID id);
}
