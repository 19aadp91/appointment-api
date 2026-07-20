package com.medisalud.appointment.application.queries.appointment.filter;

import com.medisalud.appointment.application.ports.input.appointment.FilterAppointmentsUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import com.medisalud.appointment.domain.model.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class FilterAppointmentsHandler implements FilterAppointmentsUseCase {

    private final AppointmentOutputPort appointmentOutputPort;

    public FilterAppointmentsHandler(AppointmentOutputPort appointmentOutputPort) {
        this.appointmentOutputPort = appointmentOutputPort;
    }

    @Override
    public List<Appointment> execute(UUID doctorId, UUID patientId, String status, LocalDate startDate, LocalDate endDate) {
        
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        List<Appointment> appointments = appointmentOutputPort.findAppointmentsByFilters(doctorId, patientId, status, startDate, endDate);

        if (appointments.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron citas para los filtros proporcionados.");
        } 
        
        return appointments;
    }
}
