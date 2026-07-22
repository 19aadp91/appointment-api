package com.medisalud.appointment.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Penalty(
    UUID id,
    Patient patient,
    Appointment appointment,
    String reason,
    LocalDateTime createdAt
) {}
