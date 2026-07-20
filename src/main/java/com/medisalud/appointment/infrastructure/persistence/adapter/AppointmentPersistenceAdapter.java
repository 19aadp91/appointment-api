package com.medisalud.appointment.infrastructure.persistence.adapter;


import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.enums.AppointmentStatus;
import com.medisalud.appointment.domain.model.Appointment;
import com.medisalud.appointment.infrastructure.Mapper.AppointmentMapperInfra;
import com.medisalud.appointment.infrastructure.exceptions.InfrastructureException;
import com.medisalud.appointment.infrastructure.exceptions.PersistenceException;
import com.medisalud.appointment.infrastructure.persistence.entity.AppointmentEntity;
import com.medisalud.appointment.infrastructure.persistence.entity.PatientEntity;
import com.medisalud.appointment.infrastructure.persistence.entity.PenaltyEntity;
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataAppointmentRepository;
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataDoctorRepository; // Asumiendo estos nombres
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataPatientRepository;
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataPenaltyRepository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AppointmentPersistenceAdapter implements AppointmentOutputPort {

    private final SpringDataAppointmentRepository repository;
    private final SpringDataPatientRepository patientRepository;
    private final SpringDataDoctorRepository doctorRepository;
    private final SpringDataPenaltyRepository penaltyRepository;

    public AppointmentPersistenceAdapter(
        SpringDataAppointmentRepository repository, 
        SpringDataPatientRepository patientRepository, 
        SpringDataDoctorRepository doctorRepository,
        SpringDataPenaltyRepository penaltyRepository) {
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.penaltyRepository = penaltyRepository;
    }

    @Override
    public boolean patientExists(UUID patientId) {
        return patientRepository.existsById(patientId);
    }

    @Override
    public boolean doctorExists(UUID doctorId) {
        return doctorRepository.existsById(doctorId);
    }

    @Override
    public Appointment save(Appointment appointment) {
        try {
            AppointmentEntity entity = AppointmentMapperInfra.toEntity(appointment);
            AppointmentEntity saved = repository.save(entity);
            return AppointmentMapperInfra.toDomain(saved);
        } catch (DataAccessException ex) {
            throw new PersistenceException("Error al guardar la cita: " + ex.getMessage());
        }
        catch (Exception e) {
            throw new InfrastructureException(e.getMessage(),500);
        }
    }

    @Override
    public List<LocalDateTime> findBookedTimesByDoctorAndRange(UUID doctorId, LocalDateTime start, LocalDateTime end) {
        return repository.findBookedDatetimes(doctorId, start, end, com.medisalud.appointment.domain.enums.AppointmentStatus.PROGRAMMED);
    }

    @Override
    public void registerPenalty(UUID appointmentId, UUID patientId, String reason) {
        try {
            PenaltyEntity penalty = new PenaltyEntity();
            
            PatientEntity patientRef = patientRepository.getReferenceById(patientId);
            AppointmentEntity appointmentRef = repository.getReferenceById(appointmentId);
            
            penalty.setPatient(patientRef);
            penalty.setAppointment(appointmentRef);
            penalty.setReason(reason);
            penalty.setCreatedAt(LocalDateTime.now());
            
            penaltyRepository.save(penalty);
        } catch (DataAccessException ex) {
            ex.printStackTrace(); 
            throw new PersistenceException("Error al registrar la penalización debido a un problema de estado de la entidad: " + ex.getMessage());
        }
        catch (Exception e) {
            throw new InfrastructureException(e.getMessage(),500);
        }
    }

    @Override
    public long countPenaltiesInLast30Days(UUID patientId, LocalDateTime since) {
        return penaltyRepository.countByPatientPatientIdAndCreatedAtAfter(patientId, since);
    }

    @Override
    public Optional<Appointment> findById(UUID appointmentId) {
        return repository.findById(appointmentId).map(AppointmentMapperInfra::toDomain);
    }

    @Override
    public void cancelAppointment(UUID appointmentId, LocalDateTime cancellationTime) {
        try {
            AppointmentEntity entity = repository.findById(appointmentId)
                    .orElseThrow(() -> new PersistenceException("No se encontró la entidad de la cita para la cancelación."));

            entity.setStatus(AppointmentStatus.CANCELLED);
            entity.setCancellationDatetime(cancellationTime);

            repository.save(entity);
        } catch (DataAccessException ex) {
            throw new PersistenceException("Error al actualizar el estado de cancelación de la cita: " + ex.getMessage());
        }
        catch (Exception e) {
            throw new InfrastructureException(e.getMessage(),500);
        }
    }

    @Override
    public boolean isDoctorOccupiedAt(UUID doctorId, LocalDateTime dateTime) {
        return repository.existsByDoctorDoctorIdAndAppointmentDatetimeAndStatus(
            doctorId, 
            dateTime, 
            AppointmentStatus.PROGRAMMED
        );
    }

    @Override
    public List<Appointment> findAppointmentsByFilters(UUID doctorId, UUID patientId, String status, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

            AppointmentStatus appointmentStatus = null;
            if (status != null && !status.isBlank()) {
                appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase().trim());
            }

            return repository.findByOptionalFilters(doctorId, patientId, appointmentStatus, startDateTime, endDateTime)
                    .stream()
                    .map(AppointmentMapperInfra::toDomain)
                    .toList();
        } catch (DataAccessException ex) {
            throw new PersistenceException("Error al actualizar el estado de cancelación de la cita: " + ex.getMessage());
        }
        catch (Exception e) {
            throw new InfrastructureException(e.getMessage(),500);
        }     
    }
}