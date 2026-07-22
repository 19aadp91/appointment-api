package com.medisalud.appointment.application.commands.appointment.create;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.medisalud.appointment.application.Mapper.appointment.AppointmentMapperAplication;
import com.medisalud.appointment.application.ports.input.appointment.CreateAppointmentUseCase;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import com.medisalud.appointment.domain.model.Appointment;
import com.medisalud.appointment.domain.model.Patient;

public class CreateAppointmentHandler implements CreateAppointmentUseCase {

    private final AppointmentOutputPort appointmentOutputPort;

    public CreateAppointmentHandler(AppointmentOutputPort appointmentOutputPort) {
        this.appointmentOutputPort = appointmentOutputPort;
    }

    @Override
    public UUID execute(CreateAppointmentCommand command) {

        // 1. Recuperar el paciente para validar su existencia y sus datos (RN-03)
        Patient patient = appointmentOutputPort.findPatientById(command.patientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("El paciente con ID '%s' no existe.", command.patientId())));

        // RN-03: Validar que la fecha de nacimiento no sea futura
        if (patient.birthDate() != null && patient.birthDate().isAfter(LocalDate.now())) {
            throw new BusinessException("No se pueden agendar citas para pacientes con fecha de nacimiento futura.");
        }

        // 2. Uso de 404 Not Found si el médico no existe
        if (!appointmentOutputPort.doctorExists(command.doctorId())) {
            throw new ResourceNotFoundException(
                    String.format("El doctor con ID '%s' no existe.", command.doctorId()));
        }

        // 3. Validaciones de disponibilidad (Doctor y Paciente)
        if (appointmentOutputPort.isDoctorOccupiedAt(command.doctorId(), command.scheduledAt())) {
            throw new ResourceConflictException("El doctor no está disponible en el horario solicitado.");
        }

        // RN-04: Un paciente no puede tener más de una cita activa en la misma franja
        // horaria
        if (appointmentOutputPort.isPatientOccupiedAt(command.patientId(), command.scheduledAt())) {
            throw new ResourceConflictException("El paciente ya tiene una cita programada en el mismo horario.");
        }

        // 4. Validación de sanciones acumuladas (últimos 30 días)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long penaltyCount = appointmentOutputPort.countPenaltiesInLast30Days(command.patientId(), thirtyDaysAgo);

        if (penaltyCount >= 3) {
            throw new BusinessException(String.format(
                    "El paciente está suspendido temporalmente para programar nuevas citas. Razón: acumula %d penalizaciones en los últimos 30 días.",
                    penaltyCount));
        }

        // 5. Creación y persistencia de la cita
        Appointment appointment = AppointmentMapperAplication.toDomain(command);
        Appointment savedAppointment = appointmentOutputPort.save(appointment);

        return savedAppointment.id();
    }
}
