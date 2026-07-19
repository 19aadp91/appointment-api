package com.medisalud.appointment.application.ports.input.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SearchAvailableSlotsUseCase {
    List<LocalDateTime> execute(UUID doctorId, LocalDate startDate, LocalDate endDate);
}
