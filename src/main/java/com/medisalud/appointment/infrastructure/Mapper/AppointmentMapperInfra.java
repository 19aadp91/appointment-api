package com.medisalud.appointment.infrastructure.Mapper;

import com.medisalud.appointment.domain.model.Appointment;
import com.medisalud.appointment.infrastructure.persistence.entity.AppointmentEntity;
import com.medisalud.appointment.infrastructure.persistence.entity.DoctorEntity;
import com.medisalud.appointment.infrastructure.persistence.entity.PatientEntity;

public final class AppointmentMapperInfra {

    private AppointmentMapperInfra() {}

    public static Appointment toDomain(AppointmentEntity entity) {
        if (entity == null) return null;
        return new Appointment(
            entity.getAppointmentId(),
            entity.getPatient().getPatientId(),
            entity.getDoctor().getDoctorId(),
            entity.getAppointmentDatetime(),
            entity.getStatus()
        );
    }

    public static AppointmentEntity toEntity(Appointment domain) {
        if (domain == null) return null;
        
        AppointmentEntity entity = new AppointmentEntity();
        entity.setAppointmentId(domain.getId());
        entity.setAppointmentDatetime(domain.getScheduledAt());
        entity.setStatus(domain.getStatus());
        
        DoctorEntity doctor = new DoctorEntity();
        doctor.setDoctorId(domain.getDoctorId());
        entity.setDoctor(doctor);
        
        PatientEntity patient = new PatientEntity();
        patient.setPatientId(domain.getPatientId());
        entity.setPatient(patient);
        
        return entity;
    }
}
