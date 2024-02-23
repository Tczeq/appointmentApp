package pl.test.coding.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.test.coding.patient.model.Patient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public List<String> findUniquePatientLastNames() {
        return patientRepository.findUniqueLastName();
    }
}
