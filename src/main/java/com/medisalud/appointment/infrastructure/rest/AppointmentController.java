package com.medisalud.appointment.infrastructure.rest;

import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentCommand;
import com.medisalud.appointment.application.commands.appointment.reschedule.RescheduleAppointmentCommand;
import com.medisalud.appointment.application.ports.input.appointment.CancelAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.CreateAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.FilterAppointmentsUseCase;
import com.medisalud.appointment.application.ports.input.appointment.RescheduleAppointmentUseCase;
import com.medisalud.appointment.application.ports.input.appointment.SearchAvailableSlotsUseCase;
import com.medisalud.appointment.domain.model.Appointment;
import com.medisalud.appointment.domain.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
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
    private final FilterAppointmentsUseCase filterAppointmentsUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createAppointment(@Valid @RequestBody CreateAppointmentCommand command) {
        UUID appointmentId = createAppointmentUseCase.execute(command);
        ApiResponse<UUID> response = ApiResponse.success(appointmentId,"Cita programada con éxito.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<ApiResponse<List<LocalDateTime>>> getAvailableSlots(
            @RequestParam UUID doctorId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
            
        List<LocalDateTime> slots = searchAvailableSlotsUseCase.execute(doctorId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(slots, "Horarios disponibles recuperados con éxito."));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(@PathVariable UUID id) {
        cancelAppointmentUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cita cancelada con éxito. Penalizaciones verificadas."));
    }

    @PostMapping("/reschedule")
    public ResponseEntity<ApiResponse<UUID>> rescheduleAppointment(@Valid @RequestBody RescheduleAppointmentCommand command) {
        UUID newAppointmentId = rescheduleAppointmentUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.success(newAppointmentId,"Cita reprogramada con éxito."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Appointment>>> getAppointments(
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
            
        List<Appointment> appointments = filterAppointmentsUseCase.execute(doctorId, patientId, status, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success(appointments, "Citas filtradas con éxito."));
    }
}