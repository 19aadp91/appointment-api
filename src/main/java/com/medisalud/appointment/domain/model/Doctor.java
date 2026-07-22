package com.medisalud.appointment.domain.model;

import java.util.UUID;

public record Doctor(
    UUID id,
    String fullName,
    String specialty,
    String phone,
    String email
) {}