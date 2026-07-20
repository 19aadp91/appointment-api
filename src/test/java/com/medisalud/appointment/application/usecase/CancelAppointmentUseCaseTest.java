package com.medisalud.appointment.application.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medisalud.appointment.application.commands.appointment.cancel.CancelAppointmentHandler;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.enums.AppointmentStatus;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import com.medisalud.appointment.domain.model.Appointment;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelAppointmentUseCaseTest {

    @Mock
    private AppointmentOutputPort appointmentOutputPort;

    @InjectMocks
    private CancelAppointmentHandler cancelAppointmentUseCase; // Ajusta al nombre de tu implementación

    @Test
    @DisplayName("Should cancel appointment successfully without penalty when notice is over 2 hours")
    void cancelSuccessfullyWithoutPenalty() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        Appointment mockAppointment = mock(Appointment.class);
        
        when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.PROGRAMMED);
        // Agendada para dentro de 5 horas (mucho más de las 2 horas límite)
        when(mockAppointment.getScheduledAt()).thenReturn(LocalDateTime.now().plusHours(5));
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act
        assertDoesNotThrow(() -> cancelAppointmentUseCase.execute(appointmentId));

        // Assert
        verify(appointmentOutputPort, times(1)).cancelAppointment(eq(appointmentId), any(LocalDateTime.class));
        verify(appointmentOutputPort, never()).registerPenalty(any(), any(), any());
    }

    @Test
    @DisplayName("Should cancel appointment and register penalty when notice is less than 2 hours")
    void cancelWithPenaltyWhenLate() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        Appointment mockAppointment = mock(Appointment.class);

        when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.PROGRAMMED);
        when(mockAppointment.getPatientId()).thenReturn(patientId);
        // Agendada para dentro de 30 minutos (menos de las 2 horas límite)
        when(mockAppointment.getScheduledAt()).thenReturn(LocalDateTime.now().plusMinutes(30));
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act
        assertDoesNotThrow(() -> cancelAppointmentUseCase.execute(appointmentId));

        // Assert
        verify(appointmentOutputPort, times(1)).registerPenalty(
                eq(appointmentId),
                eq(patientId),
                contains("Cancelación tardía")
        );
        verify(appointmentOutputPort, times(1)).cancelAppointment(eq(appointmentId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment does not exist")
    void throwNotFoundWhenAppointmentDoesNotExist() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> cancelAppointmentUseCase.execute(appointmentId));

        assertEquals(String.format("No se encontró la cita con ID '%s'.", appointmentId), exception.getMessage());
        verify(appointmentOutputPort, never()).cancelAppointment(any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when appointment is already cancelled")
    void throwConflictWhenAlreadyCancelled() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        Appointment mockAppointment = mock(Appointment.class);

        when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.CANCELLED);
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class, 
                () -> cancelAppointmentUseCase.execute(appointmentId));

        assertEquals("La cita ya está cancelada.", exception.getMessage());
        verify(appointmentOutputPort, never()).cancelAppointment(any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when appointment is already attended")
    void throwConflictWhenAlreadyAttended() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        Appointment mockAppointment = mock(Appointment.class);

        when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.ATTENDED);
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class, 
                () -> cancelAppointmentUseCase.execute(appointmentId));

        assertEquals("No se puede cancelar una cita que ya ha sido completada.", exception.getMessage());
        verify(appointmentOutputPort, never()).cancelAppointment(any(), any());
    }
}
