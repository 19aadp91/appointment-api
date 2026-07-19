package com.medisalud.appointment.infrastructure.global;

import com.medisalud.appointment.application.commands.doctor.create.CreateDoctorHandler;
import com.medisalud.appointment.application.ports.input.doctor.CreateDoctorUseCase;
import com.medisalud.appointment.application.ports.output.doctor.DoctorOutputPort;

import jakarta.transaction.Transactional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DoctorConfig {
    @Bean
    @Transactional
    public CreateDoctorUseCase createDoctorUseCase(DoctorOutputPort doctorOutputPort) {
        return new CreateDoctorHandler(doctorOutputPort);
    }
}
