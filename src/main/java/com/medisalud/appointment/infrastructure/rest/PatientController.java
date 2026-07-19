package com.medisalud.appointment.infrastructure.rest;

import com.medisalud.appointment.application.commands.patient.create.CreatePatientCommand;
import com.medisalud.appointment.application.ports.input.patient.CreatePatientUseCase;
import com.medisalud.appointment.domain.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final CreatePatientUseCase createPatientUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createPatient(@Valid @RequestBody CreatePatientCommand command) {
        UUID patientId = createPatientUseCase.execute(command);
        ApiResponse<UUID> response = ApiResponse.success(patientId,"Paciente registrado con éxito.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
