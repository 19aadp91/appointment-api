package com.medisalud.appointment.infrastructure.persistence.adapter;

import com.medisalud.appointment.application.ports.output.patient.PatientOutputPort;
import com.medisalud.appointment.domain.model.Patient;
import com.medisalud.appointment.infrastructure.Mapper.PatientMapperInfra;
import com.medisalud.appointment.infrastructure.exceptions.InfrastructureException;
import com.medisalud.appointment.infrastructure.exceptions.PersistenceException;
import com.medisalud.appointment.infrastructure.persistence.entity.PatientEntity;
import com.medisalud.appointment.infrastructure.persistence.repository.SpringDataPatientRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class PatientPersistenceAdapter implements PatientOutputPort {

    private final SpringDataPatientRepository repository;

    public PatientPersistenceAdapter(SpringDataPatientRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return repository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Patient save(Patient patient) {
        try {
            PatientEntity entity = PatientMapperInfra.toEntity(patient);
            if (entity.getPatientId() == null) {
                entity.setCreatedBy("SYSTEM_USER");
            } else {
                entity.setUpdatedBy("SYSTEM_USER");
            }
            PatientEntity saved = repository.save(entity);
            return PatientMapperInfra.toDomain(saved);
        } catch (DataAccessException ex) {
            throw new PersistenceException("Error al guardar el paciente en la base de datos: " + ex.getMessage());
        }
        catch (Exception e) {
            throw new InfrastructureException(e.getMessage(),500);
        }
    }
}
