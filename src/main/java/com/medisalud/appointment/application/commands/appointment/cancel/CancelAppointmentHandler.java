package com.medisalud.appointment.application.commands.appointment.cancel;

import com.medisalud.appointment.application.ports.input.appointment.CancelAppointmentUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.enums.AppointmentStatus;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import com.medisalud.appointment.domain.model.Appointment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class CancelAppointmentHandler implements CancelAppointmentUseCase {

    private final AppointmentOutputPort appointmentOutputPort;

    public CancelAppointmentHandler(AppointmentOutputPort appointmentOutputPort) {
        this.appointmentOutputPort = appointmentOutputPort;
    }

    @Override
    public void execute(UUID appointmentId) {
        // 1. Uso de 404 Not Found si la cita no existe
        Appointment appointment = appointmentOutputPort.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No se encontró la cita con ID '%s'.", appointmentId)));

        // 2. Uso de 409 Conflict si el estado actual de la cita no permite cancelarla
        if (appointment.status() == AppointmentStatus.CANCELLED) {
            throw new ResourceConflictException("La cita ya está cancelada.");
        }
        if (appointment.status() == AppointmentStatus.ATTENDED) {
            throw new ResourceConflictException("No se puede cancelar una cita que ya ha sido completada.");
        }

        LocalDateTime now = LocalDateTime.now();

        long minutesToAppointment = ChronoUnit.MINUTES.between(now, appointment.scheduledAt());
        if (minutesToAppointment < 120) {
            appointmentOutputPort.registerPenalty(
                appointmentId,
                appointment.patientId(), 
                "Cancelación tardía (Menos de 2 horas antes de la hora programada)"
            );
        }

        appointmentOutputPort.cancelAppointment(appointmentId, now);
    }
}
