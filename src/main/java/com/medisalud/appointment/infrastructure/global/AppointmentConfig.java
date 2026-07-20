package com.medisalud.appointment.infrastructure.global;

import com.medisalud.appointment.application.commands.appointment.cancel.CancelAppointmentHandler;
import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentHandler;
import com.medisalud.appointment.application.commands.appointment.reschedule.RescheduleAppointmentHandler;
import com.medisalud.appointment.application.commands.appointment.search.SearchAvailableSlotsHandler;
import com.medisalud.appointment.application.ports.input.appointment.CancelAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.CreateAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.FilterAppointmentsUseCase;
import com.medisalud.appointment.application.ports.input.appointment.RescheduleAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.SearchAvailableSlotsUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.application.queries.appointment.filter.FilterAppointmentsHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class AppointmentConfig {

    @Bean
    @Transactional
    public CreateAppointmentUseCase createAppointmentUseCase(AppointmentOutputPort appointmentOutputPort) {
        return new CreateAppointmentHandler(appointmentOutputPort);
    }

    @Bean
    public SearchAvailableSlotsUseCase searchAvailableSlotsUseCase(AppointmentOutputPort appointmentOutputPort) {
        return new SearchAvailableSlotsHandler(appointmentOutputPort);
    }

    @Bean
    @Transactional
    public CancelAppointmentUseCase cancelAppointmentUseCase(AppointmentOutputPort appointmentOutputPort) {
        return new CancelAppointmentHandler(appointmentOutputPort);
    }

    @Bean
    @Transactional
    public RescheduleAppointmentUseCase rescheduleAppointmentUseCase(AppointmentOutputPort appointmentOutputPort) {
        return new RescheduleAppointmentHandler(appointmentOutputPort);
    }

    @Bean
    public FilterAppointmentsUseCase filterAppointmentsUseCase(AppointmentOutputPort appointmentOutputPort) {
        return new FilterAppointmentsHandler(appointmentOutputPort);
    }
}