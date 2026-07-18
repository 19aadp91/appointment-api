package com.medisalud.appointment.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Penalty {

    private UUID id;
    private Patient patient;
    private Appointment appointment;
    private String reason;
    private LocalDateTime createdAt;

    public Penalty(
            UUID id,
            Patient patient,
            Appointment appointment,
            String reason,
            LocalDateTime createdAt) {

        this.id = id;
        this.patient = patient;
        this.appointment = appointment;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
