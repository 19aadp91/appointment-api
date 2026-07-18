package com.medisalud.appointment.domain.errorMessage;

public record DomainErrorRecord(String code, String message) {

    public static final class Validation {
        // Prevent instantiation
        private Validation() {}

        public static DomainErrorRecord requiredField(String field) {
            return new DomainErrorRecord("Validation.Required", String.format("The field '%s' is required.", field));
        }

        public static DomainErrorRecord invalidFormat(String field) {
            return new DomainErrorRecord("Validation.InvalidFormat", String.format("The format of the field '%s' is invalid.", field));
        }
    }

    public static final class System {
        // Prevent instantiation
        private System() {}

        public static DomainErrorRecord internalError() {
            return new DomainErrorRecord("System.InternalError", "An internal server error occurred. Please try again later.");
        }
    }
}