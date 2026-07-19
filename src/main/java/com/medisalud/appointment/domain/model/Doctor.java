package com.medisalud.appointment.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Doctor {
    private final UUID id;
    private final String fullName;
    private final String specialty;
    private final String phone;
    private final String email;
}