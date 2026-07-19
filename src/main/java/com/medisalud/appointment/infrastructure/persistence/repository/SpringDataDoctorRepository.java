package com.medisalud.appointment.infrastructure.persistence.repository;

import com.medisalud.appointment.infrastructure.persistence.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SpringDataDoctorRepository extends JpaRepository<DoctorEntity, UUID> {
    boolean existsByEmail(String email);
}
