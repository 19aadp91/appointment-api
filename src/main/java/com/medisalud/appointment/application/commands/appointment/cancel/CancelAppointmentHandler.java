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
                        String.format("Appointment with ID '%s' not found.", appointmentId)));

        // 2. Uso de 409 Conflict si el estado actual de la cita no permite cancelarla
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResourceConflictException("The appointment is already canceled.");
        }
        if (appointment.getStatus() == AppointmentStatus.ATTENDED) {
            throw new ResourceConflictException("Cannot cancel an appointment that has already been completed.");
        }

        LocalDateTime now = LocalDateTime.now();

        long minutesToAppointment = ChronoUnit.MINUTES.between(now, appointment.getScheduledAt());
        if (minutesToAppointment < 120) {
            appointmentOutputPort.registerPenalty(
                appointmentId,
                appointment.getPatientId(), 
                "Late cancellation (Less than 2 hours before the scheduled time)"
            );
        }

        appointmentOutputPort.cancelAppointment(appointmentId, now);
    }
}
