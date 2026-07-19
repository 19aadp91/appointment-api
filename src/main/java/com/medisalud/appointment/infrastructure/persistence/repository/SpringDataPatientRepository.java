package com.medisalud.appointment.infrastructure.persistence.repository;

import com.medisalud.appointment.infrastructure.persistence.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataPatientRepository extends JpaRepository<PatientEntity, UUID> {
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);
}