package com.hcdm.access.management.service.service.impl;

import com.hcdm.access.management.service.dto.ClientDto;
import com.hcdm.access.management.service.dto.CreateClientRequest;
import com.hcdm.access.management.service.entity.Client;
import com.hcdm.access.management.service.entity.ClientStatus;
import com.hcdm.access.management.service.repository.ClientRepository;
import com.hcdm.access.management.service.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "clientById", allEntries = true),
            @CacheEvict(value = "allClients", allEntries = true)
    })
    public ClientDto createClient(CreateClientRequest request) {
        if (clientRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Client with the same name already exists");
        }

        Client client = Client.builder()
                .name(request.getName())
                .status(ClientStatus.ACTIVE)
                .build();

        clientRepository.save(client);

        return mapToDto(client);
    }

    @Override
    @Cacheable(value = "allClients")
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "clientById", key = "#id"),
            @CacheEvict(value = "allClients", allEntries = true)
    })
    public ClientDto updateClient(UUID id, CreateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setName(request.getName());
        return mapToDto(clientRepository.save(client));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "clientById", key = "#id"),
            @CacheEvict(value = "allClients", allEntries = true)
    })
    public ClientDto updateClientStatus(UUID id, ClientStatus status) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setStatus(status);
        return mapToDto(clientRepository.save(client));
    }


    @Override
    @Cacheable(value = "clientById", key = "#id")
    @Transactional(readOnly = true)
    public ClientDto getClientById(UUID id) {
        return clientRepository.findById(id)
                .map(client -> ClientDto.builder()
                        .id(client.getId())
                        .name(client.getName())
                        .status(client.getStatus())
                        .createdAt(client.getCreatedAt())
                        .build())
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    private ClientDto mapToDto(Client client) {
        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .status(client.getStatus())
                .createdAt(client.getCreatedAt())
                .build();
    }
}