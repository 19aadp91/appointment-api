package com.medisalud.appointment.application.commands.appointment.create;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentCommand(
    @NotNull(message = "El ID del paciente es obligatorio.")
    UUID patientId,

    @NotNull(message = "El ID del doctor es obligatorio.")
    UUID doctorId,

    @NotNull(message = "La fecha y hora son obligatorias.")
    @Future(message = "La fecha de la cita debe ser en el futuro.")
    LocalDateTime scheduledAt
) {}