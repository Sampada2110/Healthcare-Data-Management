package com.hcdm.access.management.service.repository;


import com.hcdm.access.management.service.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository  extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
    List<AppUser> findByClientId(UUID clientId);
    Optional<AppUser> findByIdAndClientId(UUID id, UUID clientId);
    boolean existsByUsername(String username);
}
