package pl.test.coding.appointment;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import pl.test.coding.appointment.model.Appointment;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    boolean existsByDoctorIdAndTermBetween(Integer doctorId, LocalDateTime start, LocalDateTime end);
    boolean existsByPatientIdAndTermBetween(Integer patientId, LocalDateTime start, LocalDateTime end);
}


