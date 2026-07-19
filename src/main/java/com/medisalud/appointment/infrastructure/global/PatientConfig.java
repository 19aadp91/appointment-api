package com.medisalud.appointment.infrastructure.global;

import com.medisalud.appointment.application.commands.patient.create.CreatePatientHandler;
import com.medisalud.appointment.application.ports.input.patient.CreatePatientUseCase;
import com.medisalud.appointment.application.ports.output.patient.PatientOutputPort;

import jakarta.transaction.Transactional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatientConfig {

    @Bean
    @Transactional
    public CreatePatientUseCase createPatientUseCase(PatientOutputPort patientOutputPort) {
        return new CreatePatientHandler(patientOutputPort);
    }
}