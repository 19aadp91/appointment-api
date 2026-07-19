package com.medisalud.appointment.application.commands.appointment.create;

import java.util.UUID;

import com.medisalud.appointment.application.Mapper.appointment.AppointmentMapperAplication;
import com.medisalud.appointment.application.ports.input.appointment.CreateAppointmentUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.model.Appointment;

public class CreateAppointmentHandler implements CreateAppointmentUseCase {

    private final AppointmentOutputPort appointmentOutputPort;

    public CreateAppointmentHandler(AppointmentOutputPort appointmentOutputPort) {
        this.appointmentOutputPort = appointmentOutputPort;
    }

    @Override
    public UUID execute(CreateAppointmentCommand command) {
        
        if (!appointmentOutputPort.patientExists(command.patientId())) {
            throw new BusinessException(String.format("Patient with ID '%s' does not exist.", command.patientId()));
        }

        if (!appointmentOutputPort.doctorExists(command.doctorId())) {
            throw new BusinessException(String.format("Doctor with ID '%s' does not exist.", command.doctorId()));
        }

        Appointment appointment = AppointmentMapperAplication.toDomain(command);
        Appointment savedAppointment = appointmentOutputPort.save(appointment);

        return savedAppointment.getId();
    }
}
