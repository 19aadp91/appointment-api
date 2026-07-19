package com.medisalud.appointment.application.commands.patient.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreatePatientCommand(
    @NotBlank(message = "Full name is required.")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters.")
    String fullName,

    @NotBlank(message = "Document number is required.")
    @Size(min = 7, max = 30, message = "Document number must be at least 7 characters.")
    String documentNumber,

    @NotBlank(message = "Phone number is required.")
    @Size(min = 7, max = 20, message = "Phone number must be at least 7 digits.")
    String phone,

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    String email,

    LocalDate birthDate
) {}
