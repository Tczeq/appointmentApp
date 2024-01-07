package pl.test.coding.patient.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Integer patientId) {
        super("Patient with ID: " + patientId + " not found.");
    }
}
