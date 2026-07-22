package com.medisalud.appointment.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.medisalud.appointment.domain.enums.AppointmentStatus;

public record Appointment(
    UUID id,
    UUID patientId,
    UUID doctorId,
    LocalDateTime scheduledAt,
    AppointmentStatus status
) {}