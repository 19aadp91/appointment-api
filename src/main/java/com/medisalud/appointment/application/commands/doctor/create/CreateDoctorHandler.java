package com.medisalud.appointment.application.commands.doctor.create;

import java.util.UUID;

import com.medisalud.appointment.application.Mapper.Doctor.DoctorMapperAplication;
import com.medisalud.appointment.application.ports.input.doctor.CreateDoctorUseCase;
import com.medisalud.appointment.application.ports.output.doctor.DoctorOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.model.Doctor;

public class CreateDoctorHandler implements CreateDoctorUseCase {

    private final DoctorOutputPort doctorOutputPort;

    public CreateDoctorHandler(DoctorOutputPort doctorOutputPort) {
        this.doctorOutputPort = doctorOutputPort;
    }

    @Override
    public UUID execute(CreateDoctorCommand command) {
        
        if (doctorOutputPort.existsByEmail(command.email())) {
            throw new BusinessException(String.format("A doctor with email '%s' already exists.", command.email()));
        }

        Doctor doctor = DoctorMapperAplication.toDomain(command);

        Doctor savedDoctor = doctorOutputPort.save(doctor);
        
        return savedDoctor.getId();
    }
}