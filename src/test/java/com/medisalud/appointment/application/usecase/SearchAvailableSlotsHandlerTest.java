package com.medisalud.appointment.application.usecase;

import com.medisalud.appointment.application.commands.appointment.search.SearchAvailableSlotsHandler;
import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.domain.exceptions.BusinessException;
import com.medisalud.appointment.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchAvailableSlotsHandlerTest {

    @Mock
    private AppointmentOutputPort appointmentOutputPort;

    @InjectMocks
    private SearchAvailableSlotsHandler searchAvailableSlotsHandler;

    private UUID doctorId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should return available slots successfully, ignoring Sundays and filtering booked times")
    void searchSlotsSuccessfully() {
        // Arrange
        // Usamos un rango fijo: del Jueves 2026-07-23 al Sábado 2026-07-25 (Viernes intermedio)
        LocalDate startDate = LocalDate.of(2026, 7, 23); 
        LocalDate endDate = LocalDate.of(2026, 7, 25);

        // Simulamos una cita ya ocupada el jueves a las 08:30
        LocalDateTime bookedSlot = LocalDateTime.of(2026, 7, 23, 8, 30);
        List<LocalDateTime> bookedTimes = List.of(bookedSlot);

        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.findBookedTimesByDoctorAndRange(eq(doctorId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bookedTimes);

        // Act
        List<LocalDateTime> availableSlots = searchAvailableSlotsHandler.execute(doctorId, startDate, endDate);

        // Assert
        assertNotNull(availableSlots);
        
        // Verificaciones lógicas:
        // Jueves (Entre semana: 8:00 a 18:00 = 20 slots de 30 min. Menos 1 reservado = 19 slots)
        // Viernes (Entre semana: 8:00 a 18:00 = 20 slots de 30 min)
        // Sábado (Fin de semana corto: 8:00 a 13:00 = 10 slots de 30 min)
        // Total esperado = 19 + 20 + 10 = 49 slots
        assertEquals(49, availableSlots.size());

        // Verificar que el slot reservado NO está en la lista resultante
        assertFalse(availableSlots.contains(bookedSlot));

        // Verificar que el primer slot del Jueves sí existe
        assertTrue(availableSlots.contains(LocalDateTime.of(2026, 7, 23, 8, 0)));
    }

    @Test
    @DisplayName("Should throw BusinessException when start date is after end date")
    void throwBusinessExceptionWhenDatesAreCrossed() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 7, 25);
        LocalDate endDate = LocalDate.of(2026, 7, 23); // Fecha fin menor a inicio

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> searchAvailableSlotsHandler.execute(doctorId, startDate, endDate));

        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());

        // Cortocircuito: No debe validar existencia ni buscar en DB
        verify(appointmentOutputPort, never()).doctorExists(any());
        verify(appointmentOutputPort, never()).findBookedTimesByDoctorAndRange(any(), any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when doctor does not exist")
    void throwNotFoundWhenDoctorDoesNotExist() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 7, 23);
        LocalDate endDate = LocalDate.of(2026, 7, 25);

        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> searchAvailableSlotsHandler.execute(doctorId, startDate, endDate));

        assertEquals(String.format("El doctor con ID '%s' no existe.", doctorId), exception.getMessage());

        // Cortocircuito: Se detiene inmediatamente y nunca consulta la agenda en DB
        verify(appointmentOutputPort, never()).findBookedTimesByDoctorAndRange(any(), any(), any());
    }

    @Test
    @DisplayName("Should skip Sundays entirely when calculating available slots")
    void shouldIgnoreSundays() {
        // Arrange
        // Domingo 26 de Julio de 2026
        LocalDate sunday = LocalDate.of(2026, 7, 26);

        when(appointmentOutputPort.doctorExists(doctorId)).thenReturn(true);
        when(appointmentOutputPort.findBookedTimesByDoctorAndRange(eq(doctorId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> searchAvailableSlotsHandler.execute(doctorId, sunday, sunday));

        assertEquals("El doctor no está disponible en el rango de fechas especificado.", exception.getMessage());
    }
}
