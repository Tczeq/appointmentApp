package pl.test.coding.appointment.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InvalidDate extends RuntimeException {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private InvalidDate(String message) {
        super(message);
    }

    public static InvalidDate forDoctor(LocalDateTime localDateTime) {
        return new InvalidDate(localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT)) + " term not available, Doctor have already appointment at this time");
    }

    public static InvalidDate forPatient(LocalDateTime localDateTime) {
        return new InvalidDate(localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT)) + " term not available, Patient have already appointment at this time");
    }
}
