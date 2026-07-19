package com.medisalud.appointment.application.commands.appointment.search;

import com.medisalud.appointment.application.ports.input.appointment.SearchAvailableSlotsUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SearchAvailableSlotsHandler implements SearchAvailableSlotsUseCase {

    private final AppointmentOutputPort appointmentOutputPort;
    
    private static final LocalTime START_WORK_HOUR = LocalTime.of(8, 0);
    private static final LocalTime END_WORK_HOUR = LocalTime.of(18, 0);
    private static final int SLOT_DURATION_MINUTES = 30;

    public SearchAvailableSlotsHandler(AppointmentOutputPort appointmentOutputPort) {
        this.appointmentOutputPort = appointmentOutputPort;
    }

    @Override
    public List<LocalDateTime> execute(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("The start date cannot be after the end date.");
        }
        if (!appointmentOutputPort.doctorExists(doctorId)) {
            throw new BusinessException(String.format("Doctor with ID '%s' does not exist.", doctorId));
        }

        LocalDateTime startDateTime = startDate.atTime(START_WORK_HOUR);
        LocalDateTime endDateTime = endDate.atTime(END_WORK_HOUR);

        List<LocalDateTime> bookedTimes = appointmentOutputPort.findBookedTimesByDoctorAndRange(doctorId, startDateTime, endDateTime);

        List<LocalDateTime> availableSlots = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime currentSlot = date.atTime(START_WORK_HOUR);
            LocalDateTime endOfWorkDay = date.atTime(END_WORK_HOUR);

            while (currentSlot.isBefore(endOfWorkDay)) {
                if (!bookedTimes.contains(currentSlot)) {
                    availableSlots.add(currentSlot);
                }
                currentSlot = currentSlot.plusMinutes(SLOT_DURATION_MINUTES);
            }
        }

        return availableSlots;
    }
}
