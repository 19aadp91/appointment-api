package com.medisalud.appointment.application.commands.appointment.create;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentCommand(
    @NotNull(message = "Patient ID is required.")
    UUID patientId,

    @NotNull(message = "Doctor ID is required.")
    UUID doctorId,

    @NotNull(message = "Date and time are required.")
    @Future(message = "Appointment date must be in the future.")
    LocalDateTime scheduledAt
) {}