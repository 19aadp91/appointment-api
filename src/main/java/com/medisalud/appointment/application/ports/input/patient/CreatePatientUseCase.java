package com.medisalud.appointment.application.ports.input.patient;

import com.medisalud.appointment.application.commands.patient.create.CreatePatientCommand;
import java.util.UUID;

public interface CreatePatientUseCase {
    UUID execute(CreatePatientCommand command);
}
