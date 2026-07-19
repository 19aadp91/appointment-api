package com.medisalud.appointment.application.ports.output.doctor;

import com.medisalud.appointment.domain.model.Doctor;

public interface DoctorOutputPort {
    Doctor save(Doctor doctor);
    boolean existsByEmail(String email);
}
