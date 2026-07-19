package com.medisalud.appointment.application.ports.output.patient;

import com.medisalud.appointment.domain.model.Patient;

public interface PatientOutputPort {
    Patient save(Patient patient);
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);
}
