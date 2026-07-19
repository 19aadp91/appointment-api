package com.medisalud.appointment.application.ports.input.doctor;

import java.util.UUID;

import com.medisalud.appointment.application.commands.doctor.create.CreateDoctorCommand;

public interface CreateDoctorUseCase {
    UUID execute(CreateDoctorCommand command);
}
