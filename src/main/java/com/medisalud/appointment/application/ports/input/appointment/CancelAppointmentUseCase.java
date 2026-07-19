package com.medisalud.appointment.application.ports.input.appointment;

import java.util.UUID;

public interface CancelAppointmentUseCase {
    void execute(UUID appointmentId);
}
