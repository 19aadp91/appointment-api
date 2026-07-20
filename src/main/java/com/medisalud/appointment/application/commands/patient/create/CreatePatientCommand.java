package com.medisalud.appointment.application.commands.patient.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreatePatientCommand(
    @NotBlank(message = "El nombre completo es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres.")
    String fullName,

    @NotBlank(message = "El número de documento es obligatorio.")
    @Size(min = 7, max = 30, message = "El número de documento debe tener al menos 7 caracteres.")
    String documentNumber,

    @NotBlank(message = "El número de teléfono es obligatorio.")
    @Size(min = 7, max = 20, message = "El número de teléfono debe tener al menos 7 dígitos.")
    String phone,

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Formato de correo electrónico no válido.")
    String email,

    LocalDate birthDate
) {}
