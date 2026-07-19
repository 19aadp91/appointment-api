package com.medisalud.appointment.infrastructure.exceptions;

public class PersistenceException extends InfrastructureException 
{
    public PersistenceException(String message) {
        super(message, 500);
    }
}
