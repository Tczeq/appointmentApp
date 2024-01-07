package pl.test.coding.doctor.exception;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(Integer doctorId) {
        super("Doctor with ID: " + doctorId + " not found.");
    }
}
