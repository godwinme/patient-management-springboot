package com.godwin.patientservice.ExceptionHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.
                        put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> emailAlreadyExistsException(EmailAlreadyExistsException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", exception.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, String>> noPatientFoundException(PatientNotFoundException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "No patient found");
        return ResponseEntity.badRequest().body(errors);
    }
}
