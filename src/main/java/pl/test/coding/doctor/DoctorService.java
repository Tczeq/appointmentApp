package pl.test.coding.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.common.ReasonSpecializationMapper;
import pl.test.coding.common.Specialization;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.doctor.model.dto.DoctorDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final ReasonSpecializationMapper reasonSpecializationMapper;


    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public List<String> findUniqueDoctorsLastNames() {
        return doctorRepository.findUniqueLastName();
    }

    public List<DoctorDto> findDoctorsByReason(ReasonForVisit reason) {
        Specialization specialization = reasonSpecializationMapper.mapReasonToSpecialization(reason);
        return doctorRepository.findBySpecialization(specialization).stream()
                .map(DoctorDto::fromEntity)
                .toList();
    }
}
