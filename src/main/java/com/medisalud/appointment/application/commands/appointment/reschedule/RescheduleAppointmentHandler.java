package com.medisalud.appointment.application.commands.appointment.reschedule;

import com.medisalud.appointment.application.ports.input.appointment.RescheduleAppointmentUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentCommand;
import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentHandler;
import com.medisalud.appointment.application.commands.appointment.cancel.CancelAppointmentHandler;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import com.medisalud.appointment.domain.model.Appointment;

import java.util.UUID;

public class RescheduleAppointmentHandler implements RescheduleAppointmentUseCase {

    private final AppointmentOutputPort appointmentOutputPort;
    // Reutilizamos los handlers existentes para asegurar que se ejecuten TODAS sus
    // reglas de negocio y penalizaciones
    private final CancelAppointmentHandler cancelHandler;
    private final CreateAppointmentHandler createHandler;

    public RescheduleAppointmentHandler(AppointmentOutputPort appointmentOutputPort) {
        this.appointmentOutputPort = appointmentOutputPort;
        this.cancelHandler = new CancelAppointmentHandler(appointmentOutputPort);
        this.createHandler = new CreateAppointmentHandler(appointmentOutputPort);
    }

    @Override
    public UUID execute(RescheduleAppointmentCommand command) {
        // 1. Uso de 404 Not Found si la cita original no existe
        Appointment oldAppointment = appointmentOutputPort.findById(command.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No se encontró la cita con ID '%s'.", command.appointmentId())));

        // 2. Uso de 409 Conflict por solapamiento de horarios del médico
        if (appointmentOutputPort.isDoctorOccupiedAt(oldAppointment.doctorId(), command.newScheduledAt())) {
            throw new ResourceConflictException("El doctor no está disponible en el nuevo horario solicitado.");
        }

        // RN-04: Uso de 409 Conflict por solapamiento de horarios del paciente en la
        // nueva fecha
        if (appointmentOutputPort.isPatientOccupiedAt(oldAppointment.patientId(), command.newScheduledAt())) {
            throw new ResourceConflictException(
                    "El paciente ya tiene una cita programada en el nuevo horario solicitado.");
        }

        // 3. Si todo está libre, procedemos a cancelar la cita previa
        cancelHandler.execute(command.appointmentId());

        // 4. Creamos la nueva cita delegando al CreateAppointmentHandler (donde se
        // validarán RN-03, sanciones, etc.)
        CreateAppointmentCommand createCommand = new CreateAppointmentCommand(
                oldAppointment.patientId(),
                oldAppointment.doctorId(),
                command.newScheduledAt());

        return createHandler.execute(createCommand);
    }
}
