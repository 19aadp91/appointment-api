package com.medisalud.appointment.domain.exceptions;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    
    private final int statusCode;

    public DomainException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
