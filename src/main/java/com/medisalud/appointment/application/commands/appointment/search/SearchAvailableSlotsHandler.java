package com.medisalud.appointment.application.commands.appointment.search;

import com.medisalud.appointment.application.ports.input.appointment.SearchAvailableSlotsUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SearchAvailableSlotsHandler implements SearchAvailableSlotsUseCase {

    private final AppointmentOutputPort appointmentOutputPort;

    private static final LocalTime START_WORK_HOUR = LocalTime.of(8, 0);
    private static final LocalTime END_WEEKDAY_HOUR = LocalTime.of(18, 0); // Lunes a Viernes
    private static final LocalTime END_SATURDAY_HOUR = LocalTime.of(13, 0); // Sábados
    private static final int SLOT_DURATION_MINUTES = 30;

    public SearchAvailableSlotsHandler(AppointmentOutputPort appointmentOutputPort) {
        this.appointmentOutputPort = appointmentOutputPort;
    }

    @Override
    public List<LocalDateTime> execute(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        // 1. Mantenemos BusinessException (400) por error de lógica en el rango de
        // fechas
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        // 2. Cambiamos a ResourceNotFoundException (404) si el médico no existe
        if (!appointmentOutputPort.doctorExists(doctorId)) {
            throw new ResourceNotFoundException(String.format("El doctor con ID '%s' no existe.", doctorId));
        }

        LocalDateTime dbStart = startDate.atTime(START_WORK_HOUR);
        LocalDateTime dbEnd = endDate.atTime(END_WEEKDAY_HOUR);

        List<LocalDateTime> bookedTimes = appointmentOutputPort.findBookedTimesByDoctorAndRange(doctorId, dbStart,
                dbEnd);

        List<LocalDateTime> availableSlots = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // Ignorar domingos si no se trabaja
            if (dayOfWeek == DayOfWeek.SUNDAY) {
                continue;
            }

            LocalTime endWorkHourForDay = (dayOfWeek == DayOfWeek.SATURDAY)
                    ? END_SATURDAY_HOUR
                    : END_WEEKDAY_HOUR;

            LocalDateTime currentSlot = date.atTime(START_WORK_HOUR);
            LocalDateTime endOfWorkDay = date.atTime(endWorkHourForDay);

            while (currentSlot.isBefore(endOfWorkDay)) {
                if (!bookedTimes.contains(currentSlot)) {
                    availableSlots.add(currentSlot);
                }
                currentSlot = currentSlot.plusMinutes(SLOT_DURATION_MINUTES);
            }
        }

        if (availableSlots.isEmpty()) {
            throw new ResourceNotFoundException("El doctor no está disponible en el rango de fechas especificado.");
        }

        return availableSlots;
    }
}