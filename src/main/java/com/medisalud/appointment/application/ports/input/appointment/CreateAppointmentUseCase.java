package com.medisalud.appointment.application.ports.input.appointment;

import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentCommand;
import java.util.UUID;

public interface CreateAppointmentUseCase {
    UUID execute(CreateAppointmentCommand command);
}
