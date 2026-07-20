package com.medisalud.appointment.application.commands.appointment.create;

import java.time.LocalDateTime;
import java.util.UUID;

import com.medisalud.appointment.application.Mapper.appointment.AppointmentMapperAplication;
import com.medisalud.appointment.application.ports.input.appointment.CreateAppointmentUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import com.medisalud.appointment.domain.model.Appointment;

public class CreateAppointmentHandler implements CreateAppointmentUseCase {

    private final AppointmentOutputPort appointmentOutputPort;

    public CreateAppointmentHandler(AppointmentOutputPort appointmentOutputPort) {
        this.appointmentOutputPort = appointmentOutputPort;
    }

    @Override
    public UUID execute(CreateAppointmentCommand command) {
        
        // 1. Uso de 404 Not Found si el paciente no existe
        if (!appointmentOutputPort.patientExists(command.patientId())) {
            throw new ResourceNotFoundException(
                String.format("El paciente con ID '%s' no existe.", command.patientId()));
        }

        // 2. Uso de 404 Not Found si el médico no existe
        if (!appointmentOutputPort.doctorExists(command.doctorId())) {
            throw new ResourceNotFoundException(
                String.format("El doctor con ID '%s' no existe.", command.doctorId()));
        }

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long penaltyCount = appointmentOutputPort.countPenaltiesInLast30Days(command.patientId(), thirtyDaysAgo);

        // 3. Uso de 400 Conflict si el paciente está suspendido por negocio
        if (penaltyCount >= 3) {
            throw new BusinessException(String.format(
                "El paciente está suspendido temporalmente para programar nuevas citas. Razón: acumula %d penalizaciones en los últimos 30 días.", 
                penaltyCount
            ));
        }

        Appointment appointment = AppointmentMapperAplication.toDomain(command);
        Appointment savedAppointment = appointmentOutputPort.save(appointment);

        return savedAppointment.getId();
    }
}
