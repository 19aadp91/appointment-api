package com.medisalud.appointment.infrastructure.rest;

import com.medisalud.appointment.application.ports.input.doctor.CreateDoctorUseCase;
import com.medisalud.appointment.application.commands.doctor.create.CreateDoctorCommand;
import com.medisalud.appointment.domain.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController 
{
    private final CreateDoctorUseCase createDoctorUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createDoctor(@Valid @RequestBody CreateDoctorCommand command) {
        
        UUID doctorId = createDoctorUseCase.execute(command);
        
        ApiResponse<UUID> response = ApiResponse.success(
            doctorId,
            "Doctor registrado con éxito."
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}