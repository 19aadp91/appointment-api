package com.medisalud.appointment.infrastructure.persistence.repository;

import com.medisalud.appointment.infrastructure.persistence.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataAppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
}
