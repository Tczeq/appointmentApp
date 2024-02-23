package pl.test.coding.appointment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.test.coding.appointment.exception.DateFromPastException;
import pl.test.coding.appointment.exception.InvalidDate;
import pl.test.coding.appointment.model.Appointment;
import pl.test.coding.appointment.model.AppointmentSortCriteria;
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
    private final CreateAppointmentMapper createAppointmentMapper;

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
        if (appointmentDto.getTerm().isBefore(LocalDateTime.now())) {
            throw new DateFromPastException(appointmentDto.getTerm());
        }
        if (appointmentRepository.existsByDoctorIdAndTermBetween(appointmentDto.getDoctorId(), appointmentDto.getTerm().minusHours(1), appointmentDto.getTerm().plusHours(1))) {
            throw InvalidDate.forDoctor(appointmentDto.getTerm());
        }
        if (appointmentRepository.existsByPatientIdAndTermBetween(appointmentDto.getPatientId(), appointmentDto.getTerm().minusHours(1), appointmentDto.getTerm().plusHours(1))) {
            throw InvalidDate.forPatient(appointmentDto.getTerm());
        }

        Doctor doctor = doctorRepository.findWithLockingById(appointmentDto.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(appointmentDto.getDoctorId()));

        Patient patient = patientRepository.findWithLockingById(appointmentDto.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(appointmentDto.getPatientId()));

        Appointment appointment = new Appointment();
        CreateAppointmentMapper.createAppointment(appointment, appointmentDto, doctor, patient);
        appointmentRepository.save(appointment);
    }

    public List<AppointmentDto> sortAndFind(AppointmentSortCriteria criteria) {
        List<AppointmentDto> appointmentsFromRepo = findAll();

        List<AppointmentDto> filteredAppointments = new ArrayList<>();

        for (AppointmentDto appointment : appointmentsFromRepo) {

            if (appointment.getIsDeleted()) {
                continue;
            }
            if (criteria.getReasonForVisit() != null) {
                continue;
            }
            if (criteria.getDoctorLastName() != null && !appointment.getDoctorLastName().toLowerCase().contains(criteria.getDoctorLastName().toLowerCase())) {
                continue;
            }
            if (criteria.getPatientLastName() != null && !appointment.getPatientLastName().toLowerCase().contains(criteria.getPatientLastName().toLowerCase())) {
                continue;
            }
            if (criteria.getAppointmentTime() != null && !appointment.getAppointmentTime().equals(criteria.getAppointmentTime())) {
                continue;
            }
            if ((criteria.getStartDate() != null && appointment.getTerm().isBefore(criteria.getStartDate())) ||
                    (criteria.getEndDate() != null && appointment.getTerm().isAfter(criteria.getEndDate()))) {
                continue;
            }
            filteredAppointments.add(appointment);
        }
        return applySorting(filteredAppointments, criteria.getSort());
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
