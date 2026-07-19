package com.medisalud.appointment.application.commands.appointment.cancel;

import com.medisalud.appointment.application.ports.input.appointment.CancelAppointmentUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.enums.AppointmentStatus;
import com.medisalud.appointment.domain.exceptions.BusinessException;
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
        Appointment appointment = appointmentOutputPort.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(String.format("Appointment with ID '%s' not found.", appointmentId)));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessException("The appointment is already canceled.");
        }
        if (appointment.getStatus() == AppointmentStatus.ATTENDED) {
            throw new BusinessException("Cannot cancel an appointment that has already been completed.");
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
