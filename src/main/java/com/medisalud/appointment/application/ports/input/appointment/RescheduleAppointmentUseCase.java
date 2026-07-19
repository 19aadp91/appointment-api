package com.medisalud.appointment.application.ports.input.appointment;

import com.medisalud.appointment.application.commands.appointment.reschedule.RescheduleAppointmentCommand;
import java.util.UUID;

public interface RescheduleAppointmentUseCase {
    UUID execute(RescheduleAppointmentCommand command);
}
