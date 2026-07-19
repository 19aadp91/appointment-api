package com.medisalud.appointment.application.Mapper.patient;

import com.medisalud.appointment.application.commands.patient.create.CreatePatientCommand;
import com.medisalud.appointment.domain.model.Patient;
import com.medisalud.appointment.infrastructure.persistence.entity.PatientEntity;

public class PatientMapperAplication {

    private PatientMapperAplication() {}

    public static Patient toDomain(CreatePatientCommand command) {
        if (command == null) return null;
        return new Patient(null, command.fullName(), command.documentNumber(), command.phone(), command.email(), command.birthDate());
    }

    public static PatientEntity toEntity(Patient patient) {
        throw new UnsupportedOperationException("Unimplemented method 'toEntity'");
    }
}
