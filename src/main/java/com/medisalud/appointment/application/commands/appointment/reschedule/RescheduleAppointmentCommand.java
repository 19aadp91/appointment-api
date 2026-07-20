package com.medisalud.appointment.application.commands.appointment.reschedule;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record RescheduleAppointmentCommand(
    @NotNull(message = "El ID de la cita es obligatorio.")
    UUID appointmentId,

    @NotNull(message = "La nueva fecha y hora son obligatorias.")
    @Future(message = "La nueva fecha de la cita debe ser en el futuro.")
    LocalDateTime newScheduledAt
) {}