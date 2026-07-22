package com.medisalud.appointment.application.commands.doctor.create;

import java.util.UUID;

import com.medisalud.appointment.application.Mapper.Doctor.DoctorMapperAplication;
import com.medisalud.appointment.application.ports.input.doctor.CreateDoctorUseCase;
import com.medisalud.appointment.application.ports.output.doctor.DoctorOutputPort;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.model.Doctor;

public class CreateDoctorHandler implements CreateDoctorUseCase {

    private final DoctorOutputPort doctorOutputPort;

    public CreateDoctorHandler(DoctorOutputPort doctorOutputPort) {
        this.doctorOutputPort = doctorOutputPort;
    }

    @Override
    public UUID execute(CreateDoctorCommand command) {
        
        // Usamos 409 Conflict porque el email ya está registrado y choca con la restricción de unicidad
        if (doctorOutputPort.existsByEmail(command.email())) {
            throw new ResourceConflictException(
                String.format("Ya existe un doctor con el correo electrónico '%s'.", command.email()));
        }

        Doctor doctor = DoctorMapperAplication.toDomain(command);

        Doctor savedDoctor = doctorOutputPort.save(doctor);
        
        return savedDoctor.id();
    }
}