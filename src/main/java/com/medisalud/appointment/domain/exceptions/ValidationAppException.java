package com.medisalud.appointment.domain.exceptions;

import lombok.Getter;
import java.util.List;

@Getter
public class ValidationAppException extends DomainException {
    
    private final List<String> errors;

    public ValidationAppException(List<String> errors) {
        super("Validation failed", 400); 
        this.errors = errors != null ? errors : List.of();
    }
}
