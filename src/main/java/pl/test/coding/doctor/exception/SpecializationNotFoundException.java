package pl.test.coding.doctor.exception;

public class SpecializationNotFoundException extends RuntimeException {
    public SpecializationNotFoundException() {
        super("No matching specialization for given reason");
    }
}
