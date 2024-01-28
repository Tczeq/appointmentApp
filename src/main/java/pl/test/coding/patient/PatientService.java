package pl.test.coding.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.test.coding.patient.model.Patient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Transactional
    public void create(Patient patient) {
        patientRepository.save(patient);
    }

    public List<String> findUniquePatientLastNames() {
        return patientRepository.findAll().stream()
                .map(Patient::getLastName)
                .distinct()
                .toList();
    }
}
