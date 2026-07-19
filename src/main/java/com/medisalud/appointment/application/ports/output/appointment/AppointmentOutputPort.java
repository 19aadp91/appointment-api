package com.medisalud.appointment.application.ports.output.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.medisalud.appointment.domain.model.Appointment;

public interface AppointmentOutputPort {
    Appointment save(Appointment appointment);
    boolean patientExists(UUID patientId);
    boolean doctorExists(UUID doctorId);
    List<LocalDateTime> findBookedTimesByDoctorAndRange(UUID doctorId, LocalDateTime start, LocalDateTime end);

    Optional<Appointment> findById(UUID appointmentId);
    void cancelAppointment(UUID appointmentId, java.time.LocalDateTime cancellationTime);
    void registerPenalty(UUID appointmentId, UUID patientId, String reason);
    long countPenaltiesInLast30Days(UUID patientId, java.time.LocalDateTime since);
    boolean isDoctorOccupiedAt(UUID doctorId, java.time.LocalDateTime dateTime);
}
