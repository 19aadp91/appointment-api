package com.medisalud.appointment.domain.model;

import java.util.UUID;

public class Doctor {

    private UUID id;
    private String fullName;
    private String specialty;
    private String phone;
    private String email;

    public Doctor(
            UUID id,
            String fullName,
            String specialty,
            String phone,
            String email) {

        this.id = id;
        this.fullName = fullName;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}