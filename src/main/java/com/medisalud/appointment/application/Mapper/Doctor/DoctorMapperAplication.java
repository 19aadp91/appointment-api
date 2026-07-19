package com.medisalud.appointment.application.Mapper.Doctor;

import com.medisalud.appointment.application.commands.doctor.create.CreateDoctorCommand;
import com.medisalud.appointment.domain.model.Doctor;

public final class DoctorMapperAplication {

    private DoctorMapperAplication() {
    }

    public static Doctor toDomain(CreateDoctorCommand command) {

        if (command == null) {
            return null;
        }

        return new Doctor(
            null,
            command.fullName(),
            command.specialty().toUpperCase(),
            command.phone(),
            command.email()
        );
    }
}