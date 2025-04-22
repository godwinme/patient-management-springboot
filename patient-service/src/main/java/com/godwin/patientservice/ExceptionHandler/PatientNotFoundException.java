package com.godwin.patientservice.ExceptionHandler;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message) {
        super(message);
    }
}
