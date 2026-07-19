package com.medisalud.appointment.infrastructure.global;

import com.medisalud.appointment.domain.exceptions.DomainException;
import com.medisalud.appointment.domain.exceptions.ValidationAppException;
import com.medisalud.appointment.domain.wrapper.ApiResponse;
import com.medisalud.appointment.infrastructure.exceptions.InfrastructureException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        ValidationAppException validationException = new ValidationAppException(errors);

        ApiResponse<Void> response = ApiResponse.failed(validationException.getMessage(), validationException.getErrors());
        
        return ResponseEntity
                .status(validationException.getStatusCode()) // Devuelve 400 Bad Request
                .body(response);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
        List<String> errorList = List.of("Business rule violation.");
        if (ex instanceof ValidationAppException validationEx) {
            errorList = validationEx.getErrors();
        }
        ApiResponse<Void> response = ApiResponse.failed(ex.getMessage(), errorList);
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ApiResponse<Void>> handleInfrastructureException(InfrastructureException ex) {
        String clientMessage = "An unexpected technical error occurred. Please try again later.";
        List<String> technicalDetails = List.of(
            String.format("Error details: %s", ex.getMessage())
        );

        ApiResponse<Void> response = ApiResponse.failed(clientMessage, technicalDetails);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        ApiResponse<Void> response = ApiResponse.failed(
            "A critical internal server error occurred.", 
            List.of("Unexpected system failure.")
        );
        
        return ResponseEntity.status(500).body(response);
    }
}