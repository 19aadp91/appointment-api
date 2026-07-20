package com.medisalud.appointment.application.usecase;


import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentCommand;
import com.medisalud.appointment.application.commands.appointment.create.CreateAppointmentHandler;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import com.medisalud.appointment.domain.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        appointmentDateTime = LocalDateTime.now().plusDays(2);
        
        // Inicializamos un comando válido por defecto para reutilizar en los tests
        command = new CreateAppointmentCommand(patientId, doctorId, appointmentDateTime);
    }

    @Test
    @DisplayName("Should create appointment successfully when all validations pass")
    void createAppointmentSuccessfully() {
        // Arrange
        UUID expectedAppointmentId = UUID.randomUUID();
        Appointment mockSavedAppointment = mock(Appointment.class);
        
        when(appointmentOutputPort.patientExists(patientId)).thenReturn(true);
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        // Simulamos que el paciente tiene menos de 3 penalizaciones (por ejemplo, 1)
        when(appointmentOutputPort.countPenaltiesInLast30Days(eq(patientId), any(LocalDateTime.class))).thenReturn(1L);
        when(mockSavedAppointment.getId()).thenReturn(expectedAppointmentId);
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
        when(appointmentOutputPort.patientExists(patientId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> createAppointmentHandler.execute(command));

        assertEquals(String.format("El paciente con ID '%s' no existe.", patientId), exception.getMessage());
        
        // Verificaciones de seguridad: no debe avanzar el flujo a validar el médico ni a guardar
        verify(appointmentOutputPort, never()).doctorExists(any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when doctor does not exist")
    void throwNotFoundWhenDoctorDoesNotExist() {
        // Arrange
        when(appointmentOutputPort.patientExists(patientId)).thenReturn(true);
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> createAppointmentHandler.execute(command));

        assertEquals(String.format("El doctor con ID '%s' no existe.", doctorId), exception.getMessage());
        
        // Verificaciones de seguridad: se frena antes de contar penalizaciones o persistir
        verify(appointmentOutputPort, never()).countPenaltiesInLast30Days(any(), any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when patient accumulates 3 or more penalties")
    void throwBusinessExceptionWhenPatientIsSuspended() {
        // Arrange
        long highPenaltyCount = 3L;
        when(appointmentOutputPort.patientExists(patientId)).thenReturn(true);
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.countPenaltiesInLast30Days(eq(patientId), any(LocalDateTime.class))).thenReturn(highPenaltyCount);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> createAppointmentHandler.execute(command));

        String expectedMessage = String.format(
            "El paciente está suspendido temporalmente para programar nuevas citas. Razón: acumula %d penalizaciones en los últimos 30 días.", 
            highPenaltyCount
        );
        assertEquals(expectedMessage, exception.getMessage());
        
        // Verificación de seguridad: el flujo se detiene por regla de negocio y nunca guarda en DB
        verify(appointmentOutputPort, never()).save(any());
    }
}