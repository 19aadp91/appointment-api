package com.medisalud.appointment.application.queries.appointment.filter;

import com.medisalud.appointment.application.ports.input.appointment.FilterAppointmentsUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
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
        // Aquí puedes meter validaciones si las fechas vienen cruzadas
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("The start date cannot be after the end date.");
        }

        List<Appointment> appointments = appointmentOutputPort.findAppointmentsByFilters(doctorId, patientId, status, startDate, endDate);

         if(appointments.size() == 0){
            throw new ResourceNotFoundException("No appointments found for the given filters.");
        } 
        
        return appointments;
    }
}
