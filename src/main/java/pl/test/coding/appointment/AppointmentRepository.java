package pl.test.coding.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.test.coding.appointment.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

}


