package com.medisalud.appointment.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Patient {
    private final UUID id;
    private final String fullName;
    private final String documentNumber;
    private final String phone;
    private final String email;
    private final LocalDate birthDate;
}
