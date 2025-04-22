package com.godwin.patientservice.service;

import com.godwin.patientservice.ExceptionHandler.EmailAlreadyExistsException;
import com.godwin.patientservice.ExceptionHandler.PatientNotFoundException;
import com.godwin.patientservice.dto.PatientRequestDTO;
import com.godwin.patientservice.dto.PatientResponseDTO;
import com.godwin.patientservice.gprc.BillingServiceGrpcClient;
import com.godwin.patientservice.kafka.KafkaProducer;
import com.godwin.patientservice.mapper.PatientMapper;
import com.godwin.patientservice.models.Patient;
import com.godwin.patientservice.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(PatientMapper::toPatientResponseDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            log.error("Email already exists");
            throw new EmailAlreadyExistsException("Email already in use");
        }

       Patient newPatient = patientRepository.save(PatientMapper.toPatientDTO(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

       return PatientMapper.toPatientResponseDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            log.error("Email already exists");
            throw new EmailAlreadyExistsException("Email already in use");
        }
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with Id " + id + " not found"));
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setAddress(patientRequestDTO.getAddress());
        Patient save = patientRepository.save(patient);
        return PatientMapper.toPatientResponseDTO(save);
    }

    public ResponseEntity<Void> deletePatient(UUID id) {
        patientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
