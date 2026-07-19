package com.medisalud.appointment.application.commands.appointment.reschedule;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record RescheduleAppointmentCommand(
    @NotNull(message = "Appointment ID is required.")
    UUID appointmentId,

    @NotNull(message = "New date and time are required.")
    @Future(message = "The new appointment date must be in the future.")
    LocalDateTime newScheduledAt
) {}