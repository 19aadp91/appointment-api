package com.medisalud.appointment.application.queries.doctor.getById;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetDoctorByIdQuery(

        @NotNull(message = "Doctor id es requerido.")
        UUID doctorId

) {
}
