package pl.test.coding.patient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.test.coding.patient.model.Patient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Test
    void testFindAll_HappyPath_ResultsInPatientFindAll() {
        //given
        List<Patient> listOfPatients = List.of(
                new Patient(1, "Karol", "Koniczyn", 0),
                new Patient(2, "Justyna", "Trwig", 0)
        );
        when(patientRepository.findAll()).thenReturn(listOfPatients);

        //when
        List<Patient> actualPatients = patientService.findAll();

        //then
        assertEquals(listOfPatients, actualPatients);
        verify(patientRepository).findAll();
    }

    @Test
    void testFindUniqueLastNames_HappyPath_ResultsInPatientFindUniqueLastNames() {
        //given
        List<String> lastNamesOfDoctors = List.of("Bogucki", "Staron");
        when(patientRepository.findUniqueLastName()).thenReturn(lastNamesOfDoctors);

        //when
        List<String> acutalNames = patientService.findUniquePatientLastNames();

        //then
        assertEquals(lastNamesOfDoctors, acutalNames);
        verify(patientRepository).findUniqueLastName();


    }
}