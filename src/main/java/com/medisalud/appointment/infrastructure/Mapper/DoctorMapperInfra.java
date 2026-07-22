package com.medisalud.appointment.infrastructure.Mapper;

import com.medisalud.appointment.domain.model.Doctor;
import com.medisalud.appointment.infrastructure.persistence.entity.DoctorEntity;

public final class DoctorMapperInfra {

    private DoctorMapperInfra() {
    }

    public static Doctor toDomain(DoctorEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Doctor(
            entity.getDoctorId(), // maps doctorId to id
            entity.getFullName(),
            entity.getSpecialty(),
            entity.getPhone(),
            entity.getEmail()
        );
    }

    public static DoctorEntity toEntity(Doctor domain) {
        if (domain == null) {
            return null;
        }
        DoctorEntity entity = new DoctorEntity();
        entity.setDoctorId(domain.id()); // maps id to doctorId
        entity.setFullName(domain.fullName());
        entity.setSpecialty(domain.specialty());
        entity.setPhone(domain.phone());
        entity.setEmail(domain.email());
        return entity;
    }
}
