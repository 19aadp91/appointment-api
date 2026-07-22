package com.medisalud.appointment.application.usecase;

import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentCommand;
import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentHandler;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import com.medisalud.appointment.domain.model.Appointment;
import com.medisalud.appointment.domain.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAppointmentHandlerTest {

    @Mock
    private AppointmentOutputPort appointmentOutputPort;

    @InjectMocks
    private CreateAppointmentHandler createAppointmentHandler;

    private CreateAppointmentCommand command;
    private UUID patientId;
    private UUID doctorId;
    private LocalDateTime appointmentDateTime;
    private Patient defaultPatient;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        appointmentDateTime = LocalDateTime.now().plusDays(2);

        command = new CreateAppointmentCommand(patientId, doctorId, appointmentDateTime);

        // Usamos el constructor @AllArgsConstructor de Patient
        defaultPatient = new Patient(
                patientId,
                "Juan Pérez",
                "12345678",
                "3001234567",
                "juan.perez@example.com",
                LocalDate.of(1995, 5, 10));
    }

    @Test
    @DisplayName("Should create appointment successfully when all validations pass")
    void createAppointmentSuccessfully() {
        // Arrange
        UUID expectedAppointmentId = UUID.randomUUID();
        Appointment mockSavedAppointment = mock(Appointment.class);

        when(appointmentOutputPort.findPatientById(patientId)).thenReturn(Optional.of(defaultPatient));
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.isDoctorOccupiedAt(doctorId, appointmentDateTime)).thenReturn(false);
        when(appointmentOutputPort.isPatientOccupiedAt(patientId, appointmentDateTime)).thenReturn(false);
        when(appointmentOutputPort.countPenaltiesInLast30Days(eq(patientId), any(LocalDateTime.class))).thenReturn(1L);
        when(mockSavedAppointment.id()).thenReturn(expectedAppointmentId);
        when(appointmentOutputPort.save(any(Appointment.class))).thenReturn(mockSavedAppointment);

        // Act
        UUID resultId = createAppointmentHandler.execute(command);

        // Assert
        assertEquals(expectedAppointmentId, resultId);
        verify(appointmentOutputPort, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when patient does not exist")
    void throwNotFoundWhenPatientDoesNotExist() {
        // Arrange
        when(appointmentOutputPort.findPatientById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> createAppointmentHandler.execute(command));

        assertEquals(String.format("El paciente con ID '%s' no existe.", patientId), exception.getMessage());

        verify(appointmentOutputPort, never()).doctorExists(any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("RN-03: Should throw BusinessException when patient has a future birth date")
    void throwBusinessExceptionWhenPatientHasFutureBirthDate() {
        // Arrange
        Patient futurePatient = new Patient(
                patientId,
                "Bebé Futuro",
                "87654321",
                "3000000000",
                "futuro@example.com",
                LocalDate.now().plusDays(1) // Fecha de nacimiento futura
        );

        when(appointmentOutputPort.findPatientById(patientId)).thenReturn(Optional.of(futurePatient));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> createAppointmentHandler.execute(command));

        assertEquals("No se pueden agendar citas para pacientes con fecha de nacimiento futura.",
                exception.getMessage());

        verify(appointmentOutputPort, never()).doctorExists(any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("RN-03: Should allow appointment creation when patient birth date is null")
    void allowAppointmentWhenPatientBirthDateIsNull() {
        // Arrange
        UUID expectedAppointmentId = UUID.randomUUID();
        Appointment mockSavedAppointment = mock(Appointment.class);

        Patient nullBirthDatePatient = new Patient(
                patientId,
                "Sin Fecha Nacimiento",
                "00000000",
                "3000000000",
                "sinfecha@example.com",
                null // Fecha de nacimiento nula
        );

        when(appointmentOutputPort.findPatientById(patientId)).thenReturn(Optional.of(nullBirthDatePatient));
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.isDoctorOccupiedAt(doctorId, appointmentDateTime)).thenReturn(false);
        when(appointmentOutputPort.isPatientOccupiedAt(patientId, appointmentDateTime)).thenReturn(false);
        when(appointmentOutputPort.countPenaltiesInLast30Days(eq(patientId), any(LocalDateTime.class))).thenReturn(0L);
        when(mockSavedAppointment.id()).thenReturn(expectedAppointmentId);
        when(appointmentOutputPort.save(any(Appointment.class))).thenReturn(mockSavedAppointment);

        // Act
        UUID resultId = createAppointmentHandler.execute(command);

        // Assert
        assertEquals(expectedAppointmentId, resultId);
        verify(appointmentOutputPort, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when doctor does not exist")
    void throwNotFoundWhenDoctorDoesNotExist() {
        // Arrange
        when(appointmentOutputPort.findPatientById(patientId)).thenReturn(Optional.of(defaultPatient));
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> createAppointmentHandler.execute(command));

        assertEquals(String.format("El doctor con ID '%s' no existe.", doctorId), exception.getMessage());

        verify(appointmentOutputPort, never()).isDoctorOccupiedAt(any(), any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when doctor is occupied at the requested time")
    void throwResourceConflictExceptionWhenDoctorIsOccupied() {
        // Arrange
        when(appointmentOutputPort.findPatientById(patientId)).thenReturn(Optional.of(defaultPatient));
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.isDoctorOccupiedAt(doctorId, appointmentDateTime)).thenReturn(true);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> createAppointmentHandler.execute(command));

        assertEquals("El doctor no está disponible en el horario solicitado.", exception.getMessage());

        verify(appointmentOutputPort, never()).isPatientOccupiedAt(any(), any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("RN-04: Should throw ResourceConflictException when patient is occupied at the requested time")
    void throwResourceConflictExceptionWhenPatientIsOccupied() {
        // Arrange
        when(appointmentOutputPort.findPatientById(patientId)).thenReturn(Optional.of(defaultPatient));
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.isDoctorOccupiedAt(doctorId, appointmentDateTime)).thenReturn(false);
        when(appointmentOutputPort.isPatientOccupiedAt(patientId, appointmentDateTime)).thenReturn(true);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> createAppointmentHandler.execute(command));

        assertEquals("El paciente ya tiene una cita programada en el mismo horario.", exception.getMessage());

        verify(appointmentOutputPort, never()).countPenaltiesInLast30Days(any(), any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when patient accumulates 3 or more penalties")
    void throwBusinessExceptionWhenPatientIsSuspended() {
        // Arrange
        long highPenaltyCount = 3L;
        when(appointmentOutputPort.findPatientById(patientId)).thenReturn(Optional.of(defaultPatient));
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.isDoctorOccupiedAt(doctorId, appointmentDateTime)).thenReturn(false);
        when(appointmentOutputPort.isPatientOccupiedAt(patientId, appointmentDateTime)).thenReturn(false);
        when(appointmentOutputPort.countPenaltiesInLast30Days(eq(patientId), any(LocalDateTime.class)))
                .thenReturn(highPenaltyCount);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> createAppointmentHandler.execute(command));

        String expectedMessage = String.format(
                "El paciente está suspendido temporalmente para programar nuevas citas. Razón: acumula %d penalizaciones en los últimos 30 días.",
                highPenaltyCount);
        assertEquals(expectedMessage, exception.getMessage());

        verify(appointmentOutputPort, never()).save(any());
    }
}