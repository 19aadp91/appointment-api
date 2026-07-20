package com.medisalud.appointment.application.usecase;

import com.medisalud.appointment.application.commands.patient.create.CreatePatientCommand;
import com.medisalud.appointment.application.commands.patient.create.CreatePatientHandler;
import com.medisalud.appointment.application.ports.output.patient.PatientOutputPort;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePatientHandlerTest {

    @Mock
    private PatientOutputPort patientOutputPort;

    @InjectMocks
    private CreatePatientHandler createPatientHandler;

    private CreatePatientCommand command;
    private final String documentNumber = "1018456789";
    private final String patientEmail = "alvaro.patient@medisalud.com";

    @BeforeEach
    void setUp() {
        command = new CreatePatientCommand(
                "Alvaro Diaz",
                documentNumber,
                "3007654321",
                patientEmail,
                LocalDate.of(1995, 5, 20)
        );
    }

    @Test
    @DisplayName("Should create patient successfully and return UUID when document and email are unique")
    void createPatientSuccessfully() {
        // Arrange
        UUID expectedPatientId = UUID.randomUUID();
        Patient mockSavedPatient = mock(Patient.class);

        when(patientOutputPort.existsByDocumentNumber(documentNumber)).thenReturn(false);
        when(patientOutputPort.existsByEmail(patientEmail)).thenReturn(false);
        when(mockSavedPatient.getId()).thenReturn(expectedPatientId);
        when(patientOutputPort.save(any(Patient.class))).thenReturn(mockSavedPatient);

        // Act
        UUID resultId = createPatientHandler.execute(command);

        // Assert
        assertNotNull(resultId);
        assertEquals(expectedPatientId, resultId);

        verify(patientOutputPort, times(1)).existsByDocumentNumber(documentNumber);
        verify(patientOutputPort, times(1)).existsByEmail(patientEmail);
        verify(patientOutputPort, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when document number already exists")
    void throwConflictWhenDocumentNumberExists() {
        // Arrange
        when(patientOutputPort.existsByDocumentNumber(documentNumber)).thenReturn(true);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> createPatientHandler.execute(command));

        assertEquals(String.format("A patient with document number '%s' already exists.", documentNumber), exception.getMessage());

        // Cortocircuito del primer 'if': No debe verificar el email ni intentar guardar
        verify(patientOutputPort, never()).existsByEmail(any());
        verify(patientOutputPort, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when email already exists")
    void throwConflictWhenEmailExists() {
        // Arrange
        when(patientOutputPort.existsByDocumentNumber(documentNumber)).thenReturn(false);
        when(patientOutputPort.existsByEmail(patientEmail)).thenReturn(true);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> createPatientHandler.execute(command));

        assertEquals(String.format("A patient with email '%s' already exists.", patientEmail), exception.getMessage());

        // Cortocircuito del segundo 'if': Verificó documento y email, pero jamás debe invocar al guardado
        verify(patientOutputPort, times(1)).existsByDocumentNumber(documentNumber);
        verify(patientOutputPort, never()).save(any(Patient.class));
    }
}
