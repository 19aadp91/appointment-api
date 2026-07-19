package com.medisalud.appointment.infrastructure.rest;

import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentCommand;
import com.medisalud.appointment.application.ports.input.appointment.CreateAppointmentUseCase;
import com.medisalud.appointment.domain.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createAppointment(@Valid @RequestBody CreateAppointmentCommand command) {
        UUID appointmentId = createAppointmentUseCase.execute(command);
        ApiResponse<UUID> response = ApiResponse.success(appointmentId,"Appointment scheduled successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}