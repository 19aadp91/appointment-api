package com.medisalud.appointment.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.medisalud.appointment.domain.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Appointment {
    private final UUID id;
    private final UUID patientId;
    private final UUID doctorId;
    private final LocalDateTime scheduledAt;
    private final AppointmentStatus status;
}