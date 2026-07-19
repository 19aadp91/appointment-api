package com.medisalud.appointment.application.ports.output.appointment;

import com.medisalud.appointment.domain.model.Appointment;

public interface AppointmentOutputPort {
    Appointment save(Appointment appointment);
    boolean patientExists(java.util.UUID patientId);
    boolean doctorExists(java.util.UUID doctorId);
}
