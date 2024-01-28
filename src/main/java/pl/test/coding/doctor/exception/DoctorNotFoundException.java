package pl.test.coding.doctor.exception;

import java.text.MessageFormat;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(Integer doctorId) {
        super(MessageFormat.format("Doctor with id={0} not found", doctorId));
    }
}
