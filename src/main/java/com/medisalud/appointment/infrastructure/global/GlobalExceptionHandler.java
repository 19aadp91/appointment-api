package com.medisalud.appointment.infrastructure.global;


import lombok.extern.slf4j.Slf4j; // Para usar el objeto 'log' directamente
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.medisalud.appointment.domain.exceptions.DomainException;
import com.medisalud.appointment.domain.exceptions.ValidationAppException;
import com.medisalud.appointment.domain.wrapper.ApiResponse;
import com.medisalud.appointment.infrastructure.exceptions.InfrastructureException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j // <-- Esto genera automáticamente la variable 'log' lista para usar
@RestControllerAdvice
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

        log.warn("Validation error standardizing request fields: {}", errors);

        ApiResponse<Void> response = ApiResponse.failed(validationException.getMessage(), validationException.getErrors());
        
        return ResponseEntity
                .status(validationException.getStatusCode()) 
                .body(response);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
        List<String> errorList = List.of("");
        if (ex instanceof ValidationAppException validationEx) {
            errorList = validationEx.getErrors();
        }

        log.warn("Domain exception caught [Code: {}]: {} - Reasons: {}", ex.getStatusCode(), ex.getMessage(), errorList);

        ApiResponse<Void> response = ApiResponse.failed(ex.getMessage(), errorList);
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ApiResponse<Void>> handleInfrastructureException(InfrastructureException ex) {
        String clientMessage = "An unexpected technical error occurred. Please try again later.";
        List<String> technicalDetails = List.of(
            String.format("Error details: %s", ex.getMessage())
        );

        log.error("Infrastructure exception caught [Status: {}]: {}", ex.getHttpStatus(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.failed(clientMessage, technicalDetails);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        
        log.error("Critical internal server error unhandled by the application:", ex);

        ApiResponse<Void> response = ApiResponse.failed(
            "A critical internal server error occurred.", 
            List.of("Unexpected system failure.")
        );
        
        return ResponseEntity.status(500).body(response);
    }
}