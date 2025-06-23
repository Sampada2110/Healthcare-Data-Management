package com.hcdm.patient.service.service;

import com.hcdm.patient.service.dto.PatientDto;
import com.hcdm.patient.service.entity.Patient;
import com.hcdm.patient.service.entity.Gender;
import com.hcdm.patient.service.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private UUID clientId;
    private Patient samplePatient;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        samplePatient = Patient.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .name("John Doe")
                .email("john@example.com")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Street")
                .postcode("123456")
                .build();
    }

    @Test
    void testRegisterPatient() {
        PatientDto dto = PatientDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Street")
                .postcode("123456")
                .build();

        when(patientRepository.save(any(Patient.class))).thenReturn(samplePatient);

        assertDoesNotThrow(() -> patientService.registerPatient(dto, clientId));
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void testGetPatientsForClient() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> mockPage = new PageImpl<>(List.of(samplePatient));

        when(patientRepository.findByClientId(clientId, pageable)).thenReturn(mockPage);

        Page<PatientDto> result = patientService.getPatientsForClient(clientId, 0, 10, null);
        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());
    }

    @Test
    void testGetPatientById() {
        UUID patientId = samplePatient.getId();
        when(patientRepository.findByIdAndClientId(patientId, clientId)).thenReturn(Optional.of(samplePatient));

        var response = patientService.getPatientById(patientId, clientId);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("John Doe", response.getBody().getName());
    }

    @Test
    void testDeletePatientById() {
        UUID patientId = samplePatient.getId();
        when(patientRepository.findByIdAndClientId(patientId, clientId)).thenReturn(Optional.of(samplePatient));

        var response = patientService.deletePatientById(patientId, clientId);
        assertEquals(204, response.getStatusCode().value());
        verify(patientRepository).delete(samplePatient);
    }

    @Test
    void testUpdatePatient() {
        UUID patientId = samplePatient.getId();
        PatientDto dto = PatientDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .gender(Gender.FEMALE)
                .dateOfBirth(LocalDate.of(1985, 5, 20))
                .address("New Address")
                .postcode("654321")
                .build();

        when(patientRepository.findByIdAndClientId(patientId, clientId)).thenReturn(Optional.of(samplePatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(samplePatient);

        var response = patientService.updatePatient(patientId, dto, clientId);
        assertEquals(200, response.getStatusCode().value());
    }
}
