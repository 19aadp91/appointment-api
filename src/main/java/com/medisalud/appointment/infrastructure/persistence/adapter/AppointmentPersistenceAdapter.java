package com.medisalud.appointment.infrastructure.persistence.adapter;


import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.model.Appointment;
import com.medisalud.appointment.infrastructure.Mapper.AppointmentMapperInfra;
import com.medisalud.appointment.infrastructure.exceptions.InfrastructureException;
import com.medisalud.appointment.infrastructure.exceptions.PersistenceException;
import com.medisalud.appointment.infrastructure.persistence.entity.AppointmentEntity;
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataAppointmentRepository;
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataDoctorRepository; // Asumiendo estos nombres
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataPatientRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class AppointmentPersistenceAdapter implements AppointmentOutputPort {

    private final SpringDataAppointmentRepository repository;
    private final SpringDataPatientRepository patientRepository;
    private final SpringDataDoctorRepository doctorRepository;

    public AppointmentPersistenceAdapter(SpringDataAppointmentRepository repository, SpringDataPatientRepository patientRepository, SpringDataDoctorRepository doctorRepository) {
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
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
            throw new PersistenceException("Error saving appointment:" + ex.getMessage());
        }
        catch (Exception e) {
            throw new InfrastructureException(e.getMessage(),500);
        }
    }
}