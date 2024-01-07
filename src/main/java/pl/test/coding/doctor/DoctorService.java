package pl.test.coding.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.common.Specialization;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.doctor.model.dto.DoctorDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Transactional
    public void create(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    public Set<String> findUniqueDoctorLastNames() {
        return doctorRepository.findAll().stream()
                .map(Doctor::getLastName)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public List<DoctorDto> findDoctorsByReason(ReasonForVisit reason) {
        Specialization specialization = mapReasonToSpecialization(reason);
        return doctorRepository.findBySpecialization(specialization).stream()
                .map(DoctorDto::fromEntity)
                .collect(Collectors.toList());
    }

    private Specialization mapReasonToSpecialization(ReasonForVisit reason) {
        return switch (reason) {
            case ANXIETY -> Specialization.PSYCHIATRY;
            case ASTHMA -> Specialization.PULMONOLOGY;
            case BACK_PROBLEM -> Specialization.CARDIOLOGY;
            case CHOLESTEROL_PROBLEM -> Specialization.DERMATOLOGY;
            case MIGRAINE, DIABETE -> Specialization.FAMILY_MEDICINE;
            case HIGH_BLOOD_PRESSURE -> Specialization.HERMATOLOGY;
            default -> throw new IllegalArgumentException("No matching specialization for given reason");
        };
    }

    @Transactional
    public void deleteById(int idToDelete) {
        doctorRepository.deleteById(idToDelete);
    }
}
