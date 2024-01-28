package pl.test.coding.patient.exception;

import java.text.MessageFormat;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Integer patientId) {
        super(MessageFormat.format("Patient with id={0} not found", patientId));
    }
}
