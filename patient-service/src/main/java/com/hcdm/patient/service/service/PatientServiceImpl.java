package com.hcdm.patient.service.service;


import com.hcdm.patient.service.dto.PatientDto;
import com.hcdm.patient.service.entity.Patient;
import com.hcdm.patient.service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private CacheManager cacheManager;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "patientsByClient", key = "#clientId"),
            @CacheEvict(value = "patientById", allEntries = true)
    })
    @Transactional
    public void registerPatient(PatientDto dto, UUID clientId) {
        Patient patient = mapToEntity(dto);
        patient.setClientId(clientId);
        mapToDto(patientRepository.save(patient));
    }

    @Override
    @Cacheable(value = "patientsByClient", key = "#clientId")
    @Transactional(readOnly = true)
    public Page<PatientDto> getPatientsForClient(UUID clientId, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> patients = (search != null && !search.isBlank()) ?
                patientRepository.findByClientIdAndNameContainingIgnoreCase(clientId, search, pageable) :
                patientRepository.findByClientId(clientId, pageable);

        return patients.map(this::mapToDto);
    }

    @Override
    @Cacheable(value = "patientById", key = "#id")
    @Transactional(readOnly = true)
    public ResponseEntity<PatientDto> getPatientById(UUID id, UUID clientId) {
        return patientRepository.findByIdAndClientId(id, clientId)
                .map(p -> ResponseEntity.ok(mapToDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "patientsByClient", key = "#clientId"),
            @CacheEvict(value = "patientById", key = "#id")
    })
    @Transactional
    public ResponseEntity<Void> deletePatientById(UUID id, UUID clientId) {
        Optional<Patient> patient = patientRepository.findByIdAndClientId(id, clientId);

        if (patient.isPresent()) {
            patientRepository.delete(patient.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "patientsByClient", key = "#clientId"),
            @CacheEvict(value = "patientById", key = "#id")
    })
    @Transactional
    public ResponseEntity<PatientDto> updatePatient(UUID id, PatientDto dto, UUID clientId) {
        return patientRepository.findByIdAndClientId(id, clientId)
                .map(patient -> {
                    mapToEntity(dto);
                    patientRepository.save(patient);

                    return ResponseEntity.ok(mapToDto(patient));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Patient mapToEntity(PatientDto dto) {
        return Patient.builder()
                .name(dto.getName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .postcode(dto.getPostcode())
                .build();
    }

    private PatientDto mapToDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .name(patient.getName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .postcode(patient.getPostcode())
                .build();
    }

}
