package com.medisalud.appointment.domain.exceptions;

public class BusinessException extends DomainException {
    public BusinessException(String message) {
        super(message, 400);
    }
}