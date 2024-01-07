package pl.test.coding.appointment.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.test.coding.appointment.model.Appointment;
import pl.test.coding.common.ReasonForVisit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
@Setter
public class AppointmentDto {

    private Integer id;
    private LocalDateTime term;
    private String formattedTerm;
    private ReasonForVisit reasonForVisit;
    private Integer appointmentTime;
    private Integer doctorId;
    private Integer patientId;
    private String doctorLastName;
    private String patientLastName;
    private Boolean isDeleted;

    public static AppointmentDto fromEntity(Appointment appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return AppointmentDto.builder()
                .id(appointment.getId())
                .term(appointment.getTerm())
                .formattedTerm(appointment.getTerm().format(formatter))
                .reasonForVisit(appointment.getReasonForVisit())
                .appointmentTime(appointment.getAppointmentTime())
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null)
                .patientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null)
                .doctorLastName(appointment.getDoctor() != null ? appointment.getDoctor().getLastName() + " " + appointment.getDoctor().getName() : null)
                .patientLastName(appointment.getPatient() != null ? appointment.getPatient().getLastName() + " " + appointment.getPatient().getName() : null)
                .isDeleted(appointment.getIsDeleted())
                .build();
    }


}
