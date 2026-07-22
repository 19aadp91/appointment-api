package com.medisalud.appointment.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record Patient(
    UUID id,
    String fullName,
    String documentNumber,
    String phone,
    String email,
    LocalDate birthDate
) {}
