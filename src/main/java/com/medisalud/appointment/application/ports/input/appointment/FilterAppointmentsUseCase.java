package com.medisalud.appointment.application.ports.input.appointment;

import com.medisalud.appointment.domain.model.Appointment;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FilterAppointmentsUseCase {
    List<Appointment> execute(UUID doctorId, UUID patientId, String status, LocalDate startDate, LocalDate endDate);
}