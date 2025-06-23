package com.hcdm.patient.service.service;


import com.hcdm.patient.service.dto.PatientDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PatientService {
    void registerPatient(PatientDto dto, UUID clientId);
    Page<PatientDto> getPatientsForClient(UUID clientId, int page, int size, String search);
    ResponseEntity<PatientDto> getPatientById(UUID id, UUID clientId);
    ResponseEntity<Void> deletePatientById(UUID id, UUID clientId);
    ResponseEntity<PatientDto> updatePatient(UUID id, PatientDto dto, UUID clientId);
}