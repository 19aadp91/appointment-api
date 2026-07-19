package com.medisalud.appointment.infrastructure.persistence.repository;

import com.medisalud.appointment.infrastructure.persistence.entity.PenaltyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public interface SpringDataPenaltyRepository extends JpaRepository<PenaltyEntity, UUID> {
    long countByPatientPatientIdAndCreatedAtAfter(UUID patientId, LocalDateTime since);
}
