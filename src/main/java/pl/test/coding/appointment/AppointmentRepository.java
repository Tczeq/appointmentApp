package pl.test.coding.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.test.coding.appointment.model.Appointment;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    boolean existsByDoctorIdAndTermBetween(Integer doctorId, LocalDateTime start, LocalDateTime end);

    boolean existsByPatientIdAndTermBetween(Integer patientId, LocalDateTime start, LocalDateTime end);
}


