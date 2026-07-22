package com.medisalud.appointment.application.usecase;

import com.medisalud.appointment.application.commands.doctor.create.CreateDoctorCommand;
import com.medisalud.appointment.application.commands.doctor.create.CreateDoctorHandler;
import com.medisalud.appointment.application.ports.output.doctor.DoctorOutputPort;
import com.medisalud.appointment.domain.exceptions.ResourceConflictException;
import com.medisalud.appointment.domain.model.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDoctorHandlerTest {

    @Mock
    private DoctorOutputPort doctorOutputPort;

    @InjectMocks
    private CreateDoctorHandler createDoctorHandler;

    private CreateDoctorCommand command;
    private String doctorEmail;

    @BeforeEach
    void setUp() {
        doctorEmail = "doctor.alvaro@medisalud.com";
        
        // Ajustado exactamente a la firma de tu record de Java
        command = new CreateDoctorCommand(
                "Dr. Alvaro Diaz",                // fullName
                "Cardiology",                     // specialty
                "+573001234567",                  // phone (cumple la regex)
                doctorEmail                       // email
        );
    }

    @Test
    @DisplayName("Should create doctor successfully and return its UUID when email is unique")
    void createDoctorSuccessfully() {
        // Arrange
        UUID expectedDoctorId = UUID.randomUUID();
        Doctor mockSavedDoctor = mock(Doctor.class);
        
        when(doctorOutputPort.existsByEmail(doctorEmail)).thenReturn(false);
        when(mockSavedDoctor.id()).thenReturn(expectedDoctorId);
        // Usamos any(Doctor.class) ya que se mapea internamente dentro del método execute
        when(doctorOutputPort.save(any(Doctor.class))).thenReturn(mockSavedDoctor);

        // Act
        UUID resultId = createDoctorHandler.execute(command);

        // Assert
        assertNotNull(resultId);
        assertEquals(expectedDoctorId, resultId);
        
        verify(doctorOutputPort, times(1)).existsByEmail(doctorEmail);
        verify(doctorOutputPort, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when the doctor email already exists")
    void throwConflictWhenEmailAlreadyExists() {
        // Arrange
        when(doctorOutputPort.existsByEmail(doctorEmail)).thenReturn(true);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> createDoctorHandler.execute(command));

        assertEquals(String.format("Ya existe un doctor con el correo electrónico '%s'.", doctorEmail), exception.getMessage());

        // Cortocircuito: Al lanzar la excepción por conflicto, jamás debe mapear ni persistir en DB
        verify(doctorOutputPort, never()).save(any(Doctor.class));
    }
}