package com.medisalud.appointment.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class InfrastructureException extends RuntimeException {
    private final int httpStatus;

    public InfrastructureException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
