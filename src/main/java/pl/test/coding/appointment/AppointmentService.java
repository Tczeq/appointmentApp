package pl.test.coding.appointment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.test.coding.appointment.exception.InvalidDate;
import pl.test.coding.appointment.model.Appointment;
import pl.test.coding.appointment.model.dto.AppointmentDto;
import pl.test.coding.doctor.DoctorRepository;
import pl.test.coding.doctor.exception.DoctorNotFoundException;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.patient.PatientRepository;
import pl.test.coding.patient.exception.PatientNotFoundException;
import pl.test.coding.patient.model.Patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor

public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public void deleteById(int idToDelete) {
        appointmentRepository.deleteById(idToDelete);
    }

    public List<AppointmentDto> findAll() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentDto::fromEntity)
                .toList();
    }

    @Transactional
    public void create(AppointmentDto appointmentDto) {
        Appointment appointment = new Appointment();

        Doctor doctor = doctorRepository.findWithLockingById(appointmentDto.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(appointmentDto.getDoctorId()));

        Patient patient = patientRepository.findWithLockingById(appointmentDto.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(appointmentDto.getPatientId()));

        if (appointmentDto.getTerm().isBefore(LocalDateTime.now())) {
            throw new InvalidDate(appointmentDto.getTerm());
        }
        appointment.setTerm(appointmentDto.getTerm());
        appointment.setReasonForVisit(appointmentDto.getReasonForVisit());
        appointment.setAppointmentTime(appointmentDto.getAppointmentTime());
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointmentRepository.save(appointment);
    }

    public List<AppointmentDto> sortAndFind(String reasonForVisit,
                                            String doctorLastName,
                                            String patientLastName,
                                            LocalDateTime startDate,
                                            LocalDateTime endDate,
                                            Integer appointmentTime,
                                            String sort) {
        List<AppointmentDto> appointmentsFromRepo = findAll();

        List<AppointmentDto> filteredAppointments = new ArrayList<>();

        for (AppointmentDto appointment : appointmentsFromRepo) {
            boolean theSame = true;

            if (appointment.getIsDeleted()) {
                continue;
            }
            if (theSame && reasonForVisit != null) {
                theSame = reasonForVisit.equals(appointment.getReasonForVisit());
            }
            if (theSame && doctorLastName != null) {
                theSame = appointment.getDoctorLastName().toLowerCase().contains(doctorLastName.toLowerCase());
            }
            if (theSame && patientLastName != null) {
                theSame = appointment.getPatientLastName().toLowerCase().contains(patientLastName.toLowerCase());
            }
            if (theSame && appointmentTime != null) {
                theSame = appointmentTime.equals(appointment.getAppointmentTime());
            }
            if (theSame && startDate != null && endDate != null) {
                LocalDateTime appointmentDateTime = appointment.getTerm();
                theSame = !appointmentDateTime.isBefore(startDate) && !appointmentDateTime.isAfter(endDate);
            }
            if (theSame) {
                filteredAppointments.add(appointment);
            }
        }
        return applySorting(filteredAppointments, sort);
    }

    public List<AppointmentDto> applySorting(List<AppointmentDto> appointments, String sort) {
        if (sort == null) return appointments;
        Comparator<AppointmentDto> comparator = getComparator(sort);
        if (comparator != null) appointments.sort(comparator);
        return appointments;
    }

    public Comparator<AppointmentDto> getComparator(String sort) {
        return switch (sort) {
            case "doctorDesc" -> Comparator.comparing(AppointmentDto::getDoctorLastName, Comparator.reverseOrder());
            case "doctorAsc" -> Comparator.comparing(AppointmentDto::getDoctorLastName);
            case "patientDesc" -> Comparator.comparing(AppointmentDto::getPatientLastName, Comparator.reverseOrder());
            case "patientAsc" -> Comparator.comparing(AppointmentDto::getPatientLastName);
            case "timeDesc" -> Comparator.comparing(AppointmentDto::getAppointmentTime, Comparator.reverseOrder());
            case "timeAsc" -> Comparator.comparing(AppointmentDto::getAppointmentTime);
            case "termDesc" -> Comparator.comparing(AppointmentDto::getTerm, Comparator.reverseOrder());
            case "termAsc" -> Comparator.comparing(AppointmentDto::getTerm);
            default -> null;
        };
    }

}
