package com.medisalud.appointment.infrastructure.persistence.repository;

import com.medisalud.appointment.domain.enums.AppointmentStatus;
import com.medisalud.appointment.infrastructure.persistence.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataAppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    @Query("SELECT a.appointmentDatetime FROM AppointmentEntity a " +
           "WHERE a.doctor.doctorId = :doctorId " +
           "AND a.appointmentDatetime >= :start " +
           "AND a.appointmentDatetime <= :end " +
           "AND a.status = :status")
    List<LocalDateTime> findBookedDatetimes(
            @Param("doctorId") UUID doctorId, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end,
            @Param("status") AppointmentStatus status);
}