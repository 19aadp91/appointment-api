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
        entity.setPatientId(domain.id());
        entity.setFullName(domain.fullName());
        entity.setDocumentNumber(domain.documentNumber());
        entity.setPhone(domain.phone());
        entity.setEmail(domain.email());
        entity.setBirthDate(domain.birthDate());
        return entity;
    }

}
