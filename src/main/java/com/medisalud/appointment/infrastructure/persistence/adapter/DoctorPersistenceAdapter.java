package com.medisalud.appointment.infrastructure.persistence.adapter;

import com.medisalud.appointment.application.ports.output.doctor.DoctorOutputPort;
import com.medisalud.appointment.domain.model.Doctor;
import com.medisalud.appointment.infrastructure.Mapper.DoctorMapperInfra;
import com.medisalud.appointment.infrastructure.exceptions.InfrastructureException;
import com.medisalud.appointment.infrastructure.exceptions.PersistenceException;
import com.medisalud.appointment.infrastructure.persistence.entity.DoctorEntity;
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataDoctorRepository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class DoctorPersistenceAdapter implements DoctorOutputPort {

    private final SpringDataDoctorRepository repository;

    public DoctorPersistenceAdapter(SpringDataDoctorRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Doctor save(Doctor doctor) {
        try 
        {
            DoctorEntity entityToSave = DoctorMapperInfra.toEntity(doctor);
            
            if (entityToSave.getDoctorId() == null) {
                entityToSave.setCreatedBy("SYSTEM_USER");
            } else {
                entityToSave.setUpdatedBy("SYSTEM_USER");
            }

            DoctorEntity savedEntity = repository.save(entityToSave);

            return DoctorMapperInfra.toDomain(savedEntity);

        } catch (DataAccessException e) 
        {
            throw new PersistenceException("Error al guardar el doctor: " + e.getMessage());
        }
        catch (Exception e) {
            throw new InfrastructureException(e.getMessage(),500);
        }
    }
}