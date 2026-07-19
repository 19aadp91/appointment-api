package com.medisalud.appointment.infrastructure.rest;

import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentCommand;
import com.medisalud.appointment.application.commands.appointment.reschedule.RescheduleAppointmentCommand;
import com.medisalud.appointment.application.ports.input.appointment.CancelAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.CreateAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.RescheduleAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.SearchAvailableSlotsUseCase;
import com.medisalud.appointment.domain.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final SearchAvailableSlotsUseCase searchAvailableSlotsUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;
    private final RescheduleAppointmentUseCase rescheduleAppointmentUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createAppointment(@Valid @RequestBody CreateAppointmentCommand command) {
        UUID appointmentId = createAppointmentUseCase.execute(command);
        ApiResponse<UUID> response = ApiResponse.success(appointmentId,"Appointment scheduled successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<ApiResponse<List<LocalDateTime>>> getAvailableSlots(
            @RequestParam UUID doctorId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
            
        List<LocalDateTime> slots = searchAvailableSlotsUseCase.execute(doctorId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(slots, "Available slots retrieved successfully."));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(@PathVariable UUID id) {
        cancelAppointmentUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Appointment canceled successfully. Penalties checked."));
    }

    @PostMapping("/reschedule")
    public ResponseEntity<ApiResponse<UUID>> rescheduleAppointment(@Valid @RequestBody RescheduleAppointmentCommand command) {
        UUID newAppointmentId = rescheduleAppointmentUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.success(newAppointmentId,"Appointment rescheduled successfully."));
    }
}