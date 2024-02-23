package pl.test.coding.doctor;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import pl.test.coding.common.Specialization;
import pl.test.coding.doctor.model.Doctor;

import java.util.List;
import java.util.Optional;


public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    List<Doctor> findBySpecialization(Specialization specialization);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Doctor> findWithLockingById(int id);


    @Query("SELECT DISTINCT doc.lastName FROM Doctor doc")
    List<String> findUniqueLastName();
}
