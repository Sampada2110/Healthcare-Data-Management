package com.hcdm.patient.service.controller;


import com.hcdm.patient.service.dto.PatientDto;
import com.hcdm.patient.service.service.PatientService;
import com.hcdm.patient.service.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {
    @Autowired
    private PatientService patientService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> register(@Valid @RequestBody PatientDto dto,
                                           @RequestHeader("Authorization") String authHeader) {
        patientService.registerPatient(dto, extractClientId(authHeader));
        return ResponseEntity.status(HttpStatus.CREATED).body("Patient registered successfully");

    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PatientDto>> listPatients(@RequestHeader("Authorization") String authHeader,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(required = false) String search) {
        return ResponseEntity.ok(patientService.getPatientsForClient(extractClientId(authHeader), page, size, search));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto> get(@PathVariable UUID id,
                                          @RequestHeader("Authorization") String authHeader) {
        return patientService.getPatientById(id, extractClientId(authHeader));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto> update(@PathVariable UUID id,
                                             @Valid @RequestBody PatientDto dto,
                                             @RequestHeader("Authorization") String authHeader) {
        return patientService.updatePatient(id, dto, extractClientId(authHeader));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @RequestHeader("Authorization") String authHeader) {
        return patientService.deletePatientById(id, extractClientId(authHeader));
    }

    private UUID extractClientId(String authHeader) {
        return jwtUtil.extractClientId(authHeader.replace("Bearer ", ""));
    }

}
