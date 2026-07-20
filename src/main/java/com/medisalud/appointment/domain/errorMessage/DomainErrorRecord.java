package com.medisalud.appointment.domain.errorMessage;

public record DomainErrorRecord(String code, String message) {

    public static final class Validation {
        // Prevent instantiation
        private Validation() {}

        public static DomainErrorRecord requiredField(String field) {
            return new DomainErrorRecord("Validation.Required", String.format("El campo '%s' es obligatorio.", field));
        }

        public static DomainErrorRecord invalidFormat(String field) {
            return new DomainErrorRecord("Validation.InvalidFormat", String.format("El formato del campo '%s' no es válido.", field));
        }
    }

    public static final class System {
        // Prevent instantiation
        private System() {}

        public static DomainErrorRecord internalError() {
            return new DomainErrorRecord("System.InternalError", "Ocurrió un error interno del servidor. Por favor, inténtelo de nuevo más tarde.");
        }
    }
}