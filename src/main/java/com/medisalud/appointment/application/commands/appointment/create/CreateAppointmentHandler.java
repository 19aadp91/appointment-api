package com.medisalud.appointment.application.commands.appointment.create;

import java.time.LocalDateTime;
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

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long penaltyCount = appointmentOutputPort.countPenaltiesInLast30Days(command.patientId(), thirtyDaysAgo);

        if (penaltyCount >= 3) {
            throw new BusinessException(String.format(
                "The patient is temporarily suspended from scheduling new appointments. Reason: accumulates %d penalties in the last 30 days.", 
                penaltyCount
            ));
        }

        Appointment appointment = AppointmentMapperAplication.toDomain(command);
        Appointment savedAppointment = appointmentOutputPort.save(appointment);

        return savedAppointment.getId();
    }
}
