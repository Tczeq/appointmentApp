package pl.test.coding.appointment.handling;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.test.coding.appointment.exception.DateFromPastException;
import pl.test.coding.appointment.exception.InvalidDate;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DateFromPastException.class, InvalidDate.class})
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "appointment/form";
    }
}
