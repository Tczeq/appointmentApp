package pl.test.coding.appointment;

import org.springframework.stereotype.Component;
import pl.test.coding.appointment.model.Appointment;
import pl.test.coding.appointment.model.dto.AppointmentDto;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.patient.model.Patient;


@Component
public class CreateAppointmentMapper {

    public static void createAppointment(Appointment appointment, AppointmentDto appointmentDto, Doctor doctor, Patient patient) {
        appointment.setTerm(appointmentDto.getTerm());
        appointment.setReasonForVisit(appointmentDto.getReasonForVisit());
        appointment.setAppointmentTime(appointmentDto.getAppointmentTime());
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setIsDeleted(false);
    }
}
