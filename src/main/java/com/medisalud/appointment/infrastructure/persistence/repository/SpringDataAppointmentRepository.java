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

       boolean existsByDoctorDoctorIdAndAppointmentDatetimeAndStatus(
                     UUID doctorId,
                     LocalDateTime dateTime,
                     com.medisalud.appointment.domain.enums.AppointmentStatus status);

       boolean existsByPatientPatientIdAndAppointmentDatetimeAndStatus(
                     UUID patientId,
                     LocalDateTime dateTime,
                     AppointmentStatus status);

       @Query("SELECT a FROM AppointmentEntity a WHERE " +
                     "(:doctorId IS NULL OR a.doctor.doctorId = :doctorId) AND " +
                     "(:patientId IS NULL OR a.patient.patientId = :patientId) AND " +
                     "(:status IS NULL OR a.status = :status) AND " +
                     "(CAST(:startDateTime AS timestamp) IS NULL OR a.appointmentDatetime >= :startDateTime) AND " +
                     "(CAST(:endDateTime AS timestamp) IS NULL OR a.appointmentDatetime <= :endDateTime)")
       List<AppointmentEntity> findByOptionalFilters(
                     @Param("doctorId") UUID doctorId,
                     @Param("patientId") UUID patientId,
                     @Param("status") AppointmentStatus status,
                     @Param("startDateTime") LocalDateTime startDateTime,
                     @Param("endDateTime") LocalDateTime endDateTime);

}