package pl.test.coding.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.common.Specialization;
import pl.test.coding.doctor.exception.SpecializationNotFoundException;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.doctor.model.dto.DoctorDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public List<String> findUniqueDoctorLastNames() {
        return doctorRepository.findAll().stream()
                .map(Doctor::getLastName)
                .distinct()
                .toList();
    }

    public List<DoctorDto> findDoctorsByReason(ReasonForVisit reason) {
        Specialization specialization = mapReasonToSpecialization(reason);
        return doctorRepository.findBySpecialization(specialization).stream()
                .map(DoctorDto::fromEntity)
                .toList();
    }

    private Specialization mapReasonToSpecialization(ReasonForVisit reason) {
        return switch (reason) {
            case ANXIETY -> Specialization.PSYCHIATRY;
            case ASTHMA -> Specialization.PULMONOLOGY;
            case BACK_PROBLEM -> Specialization.CARDIOLOGY;
            case CHOLESTEROL_PROBLEM -> Specialization.DERMATOLOGY;
            case MIGRAINE, DIABETE -> Specialization.FAMILY_MEDICINE;
            case HIGH_BLOOD_PRESSURE -> Specialization.HERMATOLOGY;
            default -> throw new SpecializationNotFoundException();
        };
    }
}
