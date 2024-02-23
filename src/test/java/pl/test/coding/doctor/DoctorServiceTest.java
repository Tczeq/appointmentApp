package pl.test.coding.doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.common.ReasonSpecializationMapper;
import pl.test.coding.common.Specialization;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.doctor.model.dto.DoctorDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @InjectMocks
    private DoctorService doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ReasonSpecializationMapper reasonSpecializationMapper;

    @Test
    void testFindAll_HappyPath_ResultsInDoctorFindAllDoctors() {
        //given
        List<Doctor> listOfDoctors = List.of(
                new Doctor(1, "Mateusz", "Konieczko", Specialization.CARDIOLOGY, 0),
                new Doctor(2, "Karol", "Justyn", Specialization.FAMILY_MEDICINE, 0)
        );
        when(doctorRepository.findAll()).thenReturn(listOfDoctors);

        //when
        List<Doctor> actualDoctors = doctorService.findAll();

        //then
        assertEquals(listOfDoctors, actualDoctors);
        verify(doctorRepository).findAll();
    }

    @Test
    void testFindUniqueDoctorsLastNames_HappyPath_ResultsInDoctorFindUniqueDoctorsLastNames() {
        //given
        List<String> lastNamesOfDoctors = List.of("Bogucki", "Staron");
        when(doctorRepository.findUniqueLastName()).thenReturn(lastNamesOfDoctors);

        //when
        List<String> actualLastNames = doctorService.findUniqueDoctorsLastNames();

        //then
        assertEquals(lastNamesOfDoctors, actualLastNames);
        verify(doctorRepository).findUniqueLastName();
    }

    @Test
    void testFindDoctorsByReason_HappyPath_ResultsInDoctorFindDoctorByReason() {
        //given

        ReasonForVisit reason = ReasonForVisit.ASTHMA;
        Specialization specialization = Specialization.PULMONOLOGY;
        List<Doctor> listOfDoctors = List.of(
                new Doctor(1, "Mateusz", "Konieczko", Specialization.PULMONOLOGY, 0),
                new Doctor(2, "Jadwiga", "Koliczyn", Specialization.PULMONOLOGY, 0)
        );
        List<DoctorDto> expectDoctors = listOfDoctors.stream().map(DoctorDto::fromEntity).toList();

        when(reasonSpecializationMapper.mapReasonToSpecialization(reason)).thenReturn(specialization);
        when(doctorRepository.findBySpecialization(specialization)).thenReturn(listOfDoctors);

        //when
        List<DoctorDto> actualDoctor = doctorService.findDoctorsByReason(reason);


        //then
        assertEquals(expectDoctors.size(), actualDoctor.size());
        verify(reasonSpecializationMapper).mapReasonToSpecialization(reason);
        verify(doctorRepository).findBySpecialization(specialization);
    }
}