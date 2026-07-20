package com.medisalud.appointment.domain.exceptions;

public class ResourceConflictException extends DomainException {
    
    public ResourceConflictException(String message) {
        super(message, 409);
    }
}