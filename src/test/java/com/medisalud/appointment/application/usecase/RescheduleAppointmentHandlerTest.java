package com.medisalud.appointment.application.usecase;

import com.medisalud.appointment.application.commands.appointment.reschedule.RescheduleAppointmentCommand;
import com.medisalud.appointment.application.commands.appointment.reschedule.RescheduleAppointmentHandler;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.enums.AppointmentStatus;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RescheduleAppointmentHandlerTest {

    @Mock
    private AppointmentOutputPort appointmentOutputPort;

    @InjectMocks
    private RescheduleAppointmentHandler rescheduleAppointmentHandler;

    private RescheduleAppointmentCommand command;
    private UUID appointmentId;
    private LocalDateTime newDateTime;
    private Appointment mockOldAppointment;
    private UUID doctorId;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        newDateTime = LocalDateTime.now().plusDays(5);

        command = new RescheduleAppointmentCommand(appointmentId, newDateTime);
        mockOldAppointment = mock(Appointment.class);
    }

    @Test
    @DisplayName("Should reschedule appointment successfully when all conditions pass")
    void rescheduleSuccessfully() {
        // Arrange
        UUID newAppointmentId = UUID.randomUUID();
        Appointment mockNewAppointment = mock(Appointment.class);

        // 💡 IMPORTANTE: Configuramos el mock de la cita antigua para que no devuelva null
        when(mockOldAppointment.getDoctorId()).thenReturn(doctorId);
        when(mockOldAppointment.getPatientId()).thenReturn(patientId);

        // Configuración para el Handler de reprogramación
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.of(mockOldAppointment));
        
        // Usa any() para la fecha para evitar problemas con cambios de milisegundos en tiempo de ejecución
        when(appointmentOutputPort.isDoctorOccupiedAt(eq(doctorId), any())).thenReturn(false);

        // Mocks requeridos internamente por el CancelAppointmentHandler
        when(mockOldAppointment.getStatus()).thenReturn(AppointmentStatus.PROGRAMMED);
        when(mockOldAppointment.getScheduledAt()).thenReturn(LocalDateTime.now().plusHours(5)); 

        // Mocks requeridos internamente por el CreateAppointmentHandler
        when(appointmentOutputPort.patientExists(patientId)).thenReturn(true);
        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.countPenaltiesInLast30Days(eq(patientId), any(LocalDateTime.class))).thenReturn(0L);
        
        when(mockNewAppointment.getId()).thenReturn(newAppointmentId);
        when(appointmentOutputPort.save(any(Appointment.class))).thenReturn(mockNewAppointment);

        // Act
        UUID resultId = rescheduleAppointmentHandler.execute(command);

        // Assert
        assertEquals(newAppointmentId, resultId);
        verify(appointmentOutputPort, times(1)).cancelAppointment(eq(appointmentId), any(LocalDateTime.class));
        verify(appointmentOutputPort, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when the original appointment does not exist")
    void throwNotFoundWhenAppointmentDoesNotExist() {
        // Arrange
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> rescheduleAppointmentHandler.execute(command));

        assertEquals(String.format("No se encontró la cita con ID '%s'.", appointmentId), exception.getMessage());
        
        verify(appointmentOutputPort, never()).isDoctorOccupiedAt(any(), any());
        verify(appointmentOutputPort, never()).cancelAppointment(any(), any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when the doctor is occupied at the new time slot")
    void throwConflictWhenDoctorIsOccupied() {
        // Arrange
        // 💡 Colocamos el stubbing de doctorId AQUÍ porque este test sí llega a leerlo de la cita
        when(mockOldAppointment.getDoctorId()).thenReturn(doctorId);
        
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.of(mockOldAppointment));
        when(appointmentOutputPort.isDoctorOccupiedAt(doctorId, newDateTime)).thenReturn(true);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class, 
                () -> rescheduleAppointmentHandler.execute(command));

        assertEquals("El doctor no está disponible en el nuevo horario solicitado.", exception.getMessage());
        
        verify(appointmentOutputPort, never()).cancelAppointment(any(), any());
        verify(appointmentOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Should bubble up exception when internal cancelHandler fails (e.g. Appointment already Attended)")
    void throwExceptionWhenInternalCancellationFails() {
        // Arrange
        // 💡 Colocamos doctorId AQUÍ también ya que pasa la validación de ocupado
        when(mockOldAppointment.getDoctorId()).thenReturn(doctorId);
        
        when(appointmentOutputPort.findById(appointmentId)).thenReturn(Optional.of(mockOldAppointment));
        when(appointmentOutputPort.isDoctorOccupiedAt(doctorId, newDateTime)).thenReturn(false);
        
        when(mockOldAppointment.getStatus()).thenReturn(AppointmentStatus.ATTENDED);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class, 
                () -> rescheduleAppointmentHandler.execute(command));

        assertEquals("No se puede cancelar una cita que ya ha sido completada.", exception.getMessage());
        
        verify(appointmentOutputPort, never()).save(any());
    }
}
