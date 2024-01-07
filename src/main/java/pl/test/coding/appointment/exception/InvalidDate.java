package pl.test.coding.appointment.exception;

import java.time.LocalDateTime;

public class InvalidDate extends RuntimeException {
    public InvalidDate(LocalDateTime localDateTime) {
        super(localDateTime + " date cannot be in the past");
    }
}
