package com.hcdm.access.management.service;

import com.hcdm.access.management.service.dto.ClientDto;
import com.hcdm.access.management.service.dto.CreateClientRequest;
import com.hcdm.access.management.service.entity.Client;
import com.hcdm.access.management.service.entity.ClientStatus;
import com.hcdm.access.management.service.repository.ClientRepository;
import com.hcdm.access.management.service.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client sampleClient;
    private UUID clientId;

    @BeforeEach
    void setup() {
        clientId = UUID.randomUUID();
        sampleClient = Client.builder()
                .id(clientId)
                .name("Test Health Org")
                .status(ClientStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateClient_Success() {
        CreateClientRequest request = new CreateClientRequest();
        request.setName("Test Health Org");

        when(clientRepository.existsByNameIgnoreCase(request.getName())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(sampleClient);

        ClientDto result = clientService.createClient(request);

        assertNotNull(result);
        assertEquals("Test Health Org", result.getName());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void testCreateClient_AlreadyExists() {
        CreateClientRequest request = new CreateClientRequest();
        request.setName("Test Health Org");

        when(clientRepository.existsByNameIgnoreCase(request.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> clientService.createClient(request));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void testGetAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(sampleClient));

        List<ClientDto> result = clientService.getAllClients();

        assertEquals(1, result.size());
        assertEquals("Test Health Org", result.get(0).getName());
    }

    @Test
    void testUpdateClient_Success() {
        CreateClientRequest request = new CreateClientRequest();
        request.setName("Updated Name");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(sampleClient));
        when(clientRepository.save(any(Client.class))).thenReturn(sampleClient);

        ClientDto result = clientService.updateClient(clientId, request);

        assertNotNull(result);
        assertEquals("Updated Name", sampleClient.getName()); // State change in original object
    }

    @Test
    void testUpdateClient_ClientNotFound() {
        CreateClientRequest request = new CreateClientRequest();
        request.setName("Doesn't Matter");

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientService.updateClient(clientId, request));
    }

    @Test
    void testUpdateClientStatus_Success() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(sampleClient));
        when(clientRepository.save(any(Client.class))).thenReturn(sampleClient);

        ClientDto result = clientService.updateClientStatus(clientId, ClientStatus.INACTIVE);

        assertNotNull(result);
        assertEquals(ClientStatus.INACTIVE, sampleClient.getStatus());
    }

    @Test
    void testGetClientById_Success() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(sampleClient));

        ClientDto result = clientService.getClientById(clientId);

        assertEquals(clientId, result.getId());
        assertEquals("Test Health Org", result.getName());
    }

    @Test
    void testGetClientById_NotFound() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clientService.getClientById(clientId));
    }
}

