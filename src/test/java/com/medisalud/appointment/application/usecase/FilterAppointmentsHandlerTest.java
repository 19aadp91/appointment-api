package com.medisalud.appointment.application.usecase;

import com.medisalud.appointment.application.ports.output.appointment.AppointmentOutputPort;
import com.medisalud.appointment.application.queries.appointment.filter.FilterAppointmentsHandler;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilterAppointmentsHandlerTest {

    @Mock
    private AppointmentOutputPort appointmentOutputPort;

    @InjectMocks
    private FilterAppointmentsHandler filterAppointmentsHandler;

    private UUID doctorId;
    private UUID patientId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        status = "CONFIRMED";
        startDate = LocalDate.of(2026, 7, 20);
        endDate = LocalDate.of(2026, 7, 25);
    }

    @Test
    @DisplayName("Should return a list of appointments when matches are found")
    void filterAppointmentsSuccessfully() {
        // Arrange
        Appointment mockAppointment = mock(Appointment.class);
        List<Appointment> expectedAppointments = List.of(mockAppointment);

        when(appointmentOutputPort.findAppointmentsByFilters(doctorId, patientId, status, startDate, endDate))
                .thenReturn(expectedAppointments);

        // Act
        List<Appointment> result = filterAppointmentsHandler.execute(doctorId, patientId, status, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(mockAppointment, result.get(0));

        verify(appointmentOutputPort, times(1))
                .findAppointmentsByFilters(doctorId, patientId, status, startDate, endDate);
    }

    @Test
    @DisplayName("Should throw BusinessException when start date is after end date")
    void throwBusinessExceptionWhenDatesAreInvalid() {
        // Arrange
        LocalDate invalidStartDate = LocalDate.of(2026, 7, 25);
        LocalDate invalidEndDate = LocalDate.of(2026, 7, 20); // Fecha fin es menor

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                filterAppointmentsHandler.execute(doctorId, patientId, status, invalidStartDate, invalidEndDate)
        );

        assertEquals("La fecha de inicio no puede ser posterior a la fecha de fin.", exception.getMessage());

        // Cortocircuito: Al fallar la validación de fechas, nunca debe ir al puerto de salida (DB)
        verify(appointmentOutputPort, never()).findAppointmentsByFilters(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no appointments match the filters")
    void throwResourceNotFoundWhenListIsEmpty() {
        // Arrange
        when(appointmentOutputPort.findAppointmentsByFilters(doctorId, patientId, status, startDate, endDate))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> 
                filterAppointmentsHandler.execute(doctorId, patientId, status, startDate, endDate)
        );

        assertEquals("No se encontraron citas para los filtros proporcionados.", exception.getMessage());
        
        verify(appointmentOutputPort, times(1))
                .findAppointmentsByFilters(doctorId, patientId, status, startDate, endDate);
    }

    @Test
    @DisplayName("Should skip date validation if start date or end date is null")
    void shouldSkipDateValidationWhenDatesAreNull() {
        // Arrange
        Appointment mockAppointment = mock(Appointment.class);
        List<Appointment> expectedAppointments = List.of(mockAppointment);

        // Simulamos un escenario donde las fechas no se envían, pero aun así hay registros
        when(appointmentOutputPort.findAppointmentsByFilters(doctorId, patientId, status, null, null))
                .thenReturn(expectedAppointments);

        // Act
        List<Appointment> result = filterAppointmentsHandler.execute(doctorId, patientId, status, null, null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        verify(appointmentOutputPort, times(1))
                .findAppointmentsByFilters(doctorId, patientId, status, null, null);
    }
}
