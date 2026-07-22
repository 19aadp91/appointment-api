package com.medisalud.appointment.application.commands.patient.create;

import java.util.UUID;

import com.medisalud.appointment.application.Mapper.patient.PatientMapperAplication;
import com.medisalud.appointment.application.ports.input.patient.CreatePatientUseCase;
import com.medisalud.appointment.application.ports.output.patient.PatientOutputPort;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.model.Patient;

public class CreatePatientHandler implements CreatePatientUseCase {

    private final PatientOutputPort patientOutputPort;

    public CreatePatientHandler(PatientOutputPort patientOutputPort) {
        this.patientOutputPort = patientOutputPort;
    }

    @Override
    public UUID execute(CreatePatientCommand command) {
        // 1. Uso de 409 Conflict por duplicación de documento de identidad
        if (patientOutputPort.existsByDocumentNumber(command.documentNumber())) {
            throw new ResourceConflictException(
                String.format("Ya existe un paciente con el número de documento '%s'.", command.documentNumber()));
        }

        // 2. Uso de 409 Conflict por duplicación de correo electrónico
        if (patientOutputPort.existsByEmail(command.email())) {
            throw new ResourceConflictException(
                String.format("Ya existe un paciente con el correo electrónico '%s'.", command.email()));
        }

        Patient patient = PatientMapperAplication.toDomain(command);
        Patient savedPatient = patientOutputPort.save(patient);

        return savedPatient.id();
    }
}
