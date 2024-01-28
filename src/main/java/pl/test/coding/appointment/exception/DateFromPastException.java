package pl.test.coding.appointment.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFromPastException extends RuntimeException {
    public DateFromPastException(LocalDateTime localDateTime) {
        super(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " date cannot be in the past");
    }
}
