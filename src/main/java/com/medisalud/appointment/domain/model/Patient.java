package com.medisalud.appointment.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class Patient {

    private UUID id;
    private String fullName;
    private String documentNumber;
    private String phone;
    private String email;
    private LocalDate birthDate;

    public Patient(
            UUID id,
            String fullName,
            String documentNumber,
            String phone,
            String email,
            LocalDate birthDate) {

        this.id = id;
        this.fullName = fullName;
        this.documentNumber = documentNumber;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
}
