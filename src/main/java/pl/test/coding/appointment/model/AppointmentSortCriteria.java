package pl.test.coding.appointment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentSortCriteria {

    String reasonForVisit;
    String doctorLastName;
    String patientLastName;
    Integer appointmentTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDateTime startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDateTime endDate;
    String sort;

}
