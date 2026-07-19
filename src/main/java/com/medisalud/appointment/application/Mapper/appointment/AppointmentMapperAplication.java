package com.medisalud.appointment.application.Mapper.appointment;

import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentCommand;
import com.medisalud.appointment.domain.enums.AppointmentStatus;
import com.medisalud.appointment.domain.model.Appointment;

public final class AppointmentMapperAplication {
    private AppointmentMapperAplication() {}

    public static Appointment toDomain(CreateAppointmentCommand command) {
        if (command == null) return null;
        return new Appointment(null, command.patientId(), command.doctorId(), command.scheduledAt(), AppointmentStatus.PROGRAMMED);
    }
}
