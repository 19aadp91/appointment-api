package com.medisalud.appointment.infrastructure.Mapper;

import com.medisalud.appointment.domain.model.Patient;
import com.medisalud.appointment.infrastructure.persistence.entity.PatientEntity;

public class PatientMapperInfra {
    
    private PatientMapperInfra() {}

    public static Patient toDomain(PatientEntity entity) {
        if (entity == null) return null;
        return new Patient(entity.getPatientId(), entity.getFullName(), entity.getDocumentNumber(), entity.getPhone(), entity.getEmail(), entity.getBirthDate());
    }

    public static PatientEntity toEntity(Patient domain) {
        if (domain == null) return null;
        PatientEntity entity = new PatientEntity();
        entity.setPatientId(domain.getId());
        entity.setFullName(domain.getFullName());
        entity.setDocumentNumber(domain.getDocumentNumber());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setBirthDate(domain.getBirthDate());
        return entity;
    }

}
