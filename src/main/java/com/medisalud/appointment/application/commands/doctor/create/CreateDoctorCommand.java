package com.medisalud.appointment.application.commands.doctor.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateDoctorCommand(

        @NotBlank(message = "El nombre completo es obligatorio.")
        @Size(min = 3, max = 100, message = "El nombre completo debe tener entre 3 y 100 caracteres.")
        String fullName,

        @NotBlank(message = "La especialidad es obligatoria.")
    @Size(max = 100, message = "La especialidad no debe exceder los 100 caracteres.")
        String specialty,

        @Size(max = 20, message = "El teléfono no debe exceder los 20 caracteres.")
        @Pattern(
                regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$",
                message = "El formato del teléfono no es válido."
        )
        String phone,

        @NotBlank(message = "El correo electrónico es obligatorio.")
        @Email(message = "El formato del correo electrónico no es válido.")
        @Size(max = 150, message = "El correo electrónico no debe exceder los 150 caracteres.")
        String email

) {
}
