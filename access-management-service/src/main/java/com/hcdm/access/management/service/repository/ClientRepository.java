package com.hcdm.access.management.service.repository;

import com.hcdm.access.management.service.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
