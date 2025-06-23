package com.hcdm.patient.service.repository;


import com.hcdm.patient.service.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Page<Patient> findByClientId(UUID clientId, Pageable pageable);
    Optional<Patient> findByIdAndClientId(UUID id, UUID clientId);
    Page<Patient> findByClientIdAndNameContainingIgnoreCase(UUID clientId, String name, Pageable pageable);
}