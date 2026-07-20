package com.medisalud.appointment.domain.exceptions;

public class ResourceNotFoundException extends DomainException {
    
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}