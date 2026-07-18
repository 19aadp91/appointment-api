package com.medisalud.appointment.domain.model;

import com.medisalud.appointment.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Appointment {

    private UUID id;
    private Doctor doctor;
    private Patient patient;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private LocalDateTime cancellationDateTime;

    public Appointment(
            UUID id,
            Doctor doctor,
            Patient patient,
            LocalDateTime appointmentDateTime,
            AppointmentStatus status,
            LocalDateTime cancellationDateTime) {

        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.cancellationDateTime = cancellationDateTime;
    }

    public UUID getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCancellationDateTime() {
        return cancellationDateTime;
    }
}