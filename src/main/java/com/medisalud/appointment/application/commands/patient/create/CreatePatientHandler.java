package com.medisalud.appointment.application.commands.patient.create;

import java.util.UUID;

import com.medisalud.appointment.application.Mapper.patient.PatientMapperAplication;
import com.medisalud.appointment.application.ports.input.patient.CreatePatientUseCase;
import com.medisalud.appointment.application.ports.output.patient.PatientOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.model.Patient;

public class CreatePatientHandler implements CreatePatientUseCase {

    private final PatientOutputPort patientOutputPort;

    public CreatePatientHandler(PatientOutputPort patientOutputPort) {
        this.patientOutputPort = patientOutputPort;
    }

    @Override
    public UUID execute(CreatePatientCommand command) {
        if (patientOutputPort.existsByDocumentNumber(command.documentNumber())) {
            throw new BusinessException(String.format("A patient with document number '%s' already exists.", command.documentNumber()));
        }

        if (patientOutputPort.existsByEmail(command.email())) {
            throw new BusinessException(String.format("A patient with email '%s' already exists.", command.email()));
        }

        Patient patient = PatientMapperAplication.toDomain(command);
        Patient savedPatient = patientOutputPort.save(patient);

        return savedPatient.getId();
    }
}
