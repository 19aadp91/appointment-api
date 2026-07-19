package com.medisalud.appointment.infrastructure.global;

import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentHandler;
import com.medisalud.appointment.application.ports.input.appointment.CreateAppointmentUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
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
}