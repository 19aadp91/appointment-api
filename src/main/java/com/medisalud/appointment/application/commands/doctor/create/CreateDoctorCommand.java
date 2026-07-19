package com.medisalud.appointment.application.commands.doctor.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateDoctorCommand(

        @NotBlank(message = "Full name es requerido.")
        @Size(min = 3, max = 100, message = "Full name Debe tener entre 3 y 100 caracteres.")
        String fullName,

        @NotBlank(message = "Specialty is required.")
    @Size(max = 100, message = "Specialty No debe exceder los 100 caracteres.")
        String specialty,

        @Size(max = 20, message = "Phone no debe exceder los 20 caracteres.")
        @Pattern(
                regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$",
                message = "El formato de Phone no es válido."
        )
        String phone,

        @NotBlank(message = "Email es requerido.")
        @Email(message = "El formato de Email no es válido.")
        @Size(max = 150, message = "Email no debe exceder los 150 caracteres.")
        String email

) {
}
