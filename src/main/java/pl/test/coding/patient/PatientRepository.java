package pl.test.coding.patient;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import pl.test.coding.patient.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Patient> findWithLockingById(int id);


    @Query("SELECT DISTINCT pat.lastName FROM Patient pat")
    List<String> findUniqueLastName();

}
