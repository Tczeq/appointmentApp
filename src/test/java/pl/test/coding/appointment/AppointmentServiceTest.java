package pl.test.coding.appointment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.test.coding.appointment.exception.DateFromPastException;
import pl.test.coding.appointment.exception.InvalidDate;
import pl.test.coding.appointment.model.Appointment;
import pl.test.coding.appointment.model.AppointmentSortCriteria;
import pl.test.coding.appointment.model.dto.AppointmentDto;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.common.Specialization;
import pl.test.coding.doctor.DoctorRepository;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.patient.PatientRepository;
import pl.test.coding.patient.model.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Captor
    private ArgumentCaptor<Appointment> appointmentArgumentCaptor;

    @Test
    void testDeleteById_HappyPath_ResultsInAppointmentDeletedById() {
        //given
        int appointmentId = 1;

        //when
        appointmentService.deleteById(appointmentId);

        //then
        verify(appointmentRepository).deleteById(appointmentId);
    }

    @Test
    void testFindAll_HappyPath_ResultsInDoctorFindAllDoctors() {
        //given
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        Doctor doctor = new Doctor(1, "Mateusz", "Konieczko", Specialization.CARDIOLOGY, 0);
        Patient patient = new Patient(1, "Mateusz", "Konieczko", 0);

        List<Appointment> appointmentList = List.of(
                new Appointment(1, localDateTime, ReasonForVisit.ASTHMA, 60, doctor, patient, false)
        );

        when(appointmentRepository.findAll()).thenReturn(appointmentList);

        List<AppointmentDto> appointmentDtoList = appointmentList.stream()
                .map(AppointmentDto::fromEntity)
                .toList();

        //when
        List<AppointmentDto> actualAppointments = appointmentService.findAll();

        //then
        assertEquals(appointmentDtoList.size(), actualAppointments.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    void testCreate_HappyPath_ResultsInAppointmentBeingSaved() {
        //given
        LocalDateTime term = LocalDateTime.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Doctor doctor = new Doctor(1, "Mateusz", "Konieczko", Specialization.CARDIOLOGY, 0);
        Patient patient = new Patient(1, "Karol", "Koniczyn", 0);

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .term(term)
                .formattedTerm(term.format(formatter))
                .reasonForVisit(ReasonForVisit.ASTHMA)
                .appointmentTime(60)
                .doctorId(1)
                .patientId(1)
                .build();

        when(doctorRepository.findWithLockingById(appointmentDto.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findWithLockingById(appointmentDto.getPatientId())).thenReturn(Optional.of(patient));

        //when
        appointmentService.create(appointmentDto);

        //then
        verify(appointmentRepository).save(appointmentArgumentCaptor.capture());

        Appointment saved = appointmentArgumentCaptor.getValue();

        assertEquals(appointmentDto.getTerm(), saved.getTerm());
        assertEquals(appointmentDto.getReasonForVisit(), saved.getReasonForVisit());
        assertEquals(appointmentDto.getAppointmentTime(), saved.getAppointmentTime());
        assertEquals(doctor.getId(), saved.getDoctor().getId());
        assertEquals(patient.getId(), saved.getPatient().getId());
    }

    @Test
    void testCreate_AppointmentNotSaved_ResultsInAppointmentTermFromPast() {
        //given
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        String exceptionMessage = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " date cannot be in the past";

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .term(localDateTime)
                .reasonForVisit(ReasonForVisit.ASTHMA)
                .appointmentTime(60)
                .doctorId(1)
                .patientId(1)
                .build();

        //when
        assertThatExceptionOfType(DateFromPastException.class)
                .isThrownBy(() -> appointmentService.create(appointmentDto))
                .withMessage(exceptionMessage);

        //then
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testCreate_AppointmentNotSaved_ResultsInAppointmentTermForDoctorIsNotAvailable() {
        //given
        LocalDateTime term = LocalDateTime.now().plusHours(2);
        LocalDateTime term1 = LocalDateTime.now().plusHours(1);
        LocalDateTime term2 = LocalDateTime.now().plusHours(3);
        String expectionMessage = term.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " term not available, Doctor have already appointment at this time";

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .term(term)
                .reasonForVisit(ReasonForVisit.ASTHMA)
                .appointmentTime(60)
                .doctorId(1)
                .patientId(1)
                .build();

        when(appointmentRepository.existsByDoctorIdAndTermBetween(appointmentDto.getDoctorId(), term1, term2)).thenReturn(true);

        //when
        assertThatExceptionOfType(InvalidDate.class)
                .isThrownBy(() -> appointmentService.create(appointmentDto))
                .withMessage(expectionMessage);

        //then
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }


    @Test
    void testCreate_AppointmentNotSaved_ResultsInAppointmentTermForPatientIsNotAvailable() {
        //given
        LocalDateTime term = LocalDateTime.now().plusHours(2);
        LocalDateTime term1 = LocalDateTime.now().plusHours(1);
        LocalDateTime term2 = LocalDateTime.now().plusHours(3);
        String expectionMessage = term.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " term not available, Patient have already appointment at this time";

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .term(term)
                .reasonForVisit(ReasonForVisit.ASTHMA)
                .appointmentTime(60)
                .doctorId(1)
                .patientId(1)
                .build();

        when(appointmentRepository.existsByPatientIdAndTermBetween(appointmentDto.getPatientId(), term1, term2)).thenReturn(true);

        //when
        assertThatExceptionOfType(InvalidDate.class)
                .isThrownBy(() -> appointmentService.create(appointmentDto))
                .withMessage(expectionMessage);

        //then
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }


    @Test
    void testGetComparator_DoctorLastNamesAscending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();

        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("doctorAsc");

        //then
        assertTrue(comparator.compare(appointment1, appointment2) > 0);
    }

    @Test
    void testGetComparator_DoctorLastNamesDescending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();

        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("doctorDesc");

        //then
        assertTrue(comparator.compare(appointment2, appointment1) > 0);
    }

    @Test
    void testGetComparator_PatientLastNamesAscending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();

        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("patientDesc");

        //then
        assertTrue(comparator.compare(appointment2, appointment1) > 0);
    }

    @Test
    void testGetComparator_PatientLastNamesDescending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();

        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("patientDesc");

        //then
        assertTrue(comparator.compare(appointment2, appointment1) > 0);
    }


    @Test
    void testGetComparator_AppointmentTimeAscending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();

        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("timeAsc");

        //then
        assertTrue(comparator.compare(appointment2, appointment1) > 0);
    }

    @Test
    void testGetComparator_AppointmentTimeDescending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();

        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("timeDesc");

        //then
        assertTrue(comparator.compare(appointment1, appointment2) > 0);
    }


    @Test
    void testGetComparator_AppointmentTermAscending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();

        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("termAsc");

        //then
        assertTrue(comparator.compare(appointment2, appointment1) > 0);
    }

    @Test
    void testGetComparator_AppointmentTermDescending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();

        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("termDesc");

        //then
        assertTrue(comparator.compare(appointment1, appointment2) > 0);
    }

    @Test
    void testGetComparator_ResultsInAppointmentNotFound() {
        //when
        Comparator<AppointmentDto> comparator = appointmentService.getComparator("doctorFirstNameAsc");
        //then
        assertNull(comparator);
    }


    @Test
    void testApplySorting_HappyPath_ResultsInSortedByDoctorLastNameAscending() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        appointmentDtoList.add(appointment1);
        appointmentDtoList.add(appointment2);

        //when
        List<AppointmentDto> sortedAppointments = appointmentService.applySorting(appointmentDtoList, "doctorAsc");

        //then
        assertEquals("Az", sortedAppointments.get(0).getDoctorLastName());
        assertEquals("Kowalski", sortedAppointments.get(1).getDoctorLastName());
    }

    @Test
    void testApplySorting_NotSorted_ResultsSortNameIsNull() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .appointmentTime(15)
                .term(LocalDateTime.now())
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .appointmentTime(60)
                .term(LocalDateTime.now().plusDays(1))
                .build();
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        appointmentDtoList.add(appointment1);
        appointmentDtoList.add(appointment2);

        //when
        List<AppointmentDto> results = appointmentService.applySorting(appointmentDtoList, null);

        //then
        assertEquals(appointmentDtoList, results);
    }


    @Test
    void testApplySorting_NotSorted_ResultsSortNameIsNotKnown() {
        //given
        AppointmentDto appointment1 = AppointmentDto.builder()
                .doctorLastName("Kowalski")
                .patientLastName("Koniczyn")
                .build();
        AppointmentDto appointment2 = AppointmentDto.builder()
                .doctorLastName("Az")
                .patientLastName("Halter")
                .build();
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        appointmentDtoList.add(appointment1);
        appointmentDtoList.add(appointment2);

        //when
        List<AppointmentDto> results = appointmentService.applySorting(appointmentDtoList, "notKnown");

        //then
        assertEquals(appointmentDtoList, results);
    }

    @Test
    void testSortAndFind_HappyPath_ResultsInAppointment() {
        //given
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        Doctor doctor1 = new Doctor(1, "Mateusz", "Konieczko", Specialization.CARDIOLOGY, 0);
        Doctor doctor2 = new Doctor(2, "Karol", "Oliesniak", Specialization.PULMONOLOGY, 0);
        Patient patient1 = new Patient(1, "Zbigniew", "Maselko", 0);
        Patient patient2 = new Patient(2, "Janusz", "Tulipan", 0);

        Appointment appointment1 = new Appointment(1, localDateTime, ReasonForVisit.ASTHMA, 60, doctor1, patient1, false);
        Appointment appointment2 = new Appointment(2, localDateTime, ReasonForVisit.ANXIETY, 60, doctor2, patient2, false);

        List<Appointment> appointmentList = List.of(appointment1, appointment2);
        when(appointmentRepository.findAll()).thenReturn(appointmentList);

        AppointmentSortCriteria appointmentSortCriteria = new AppointmentSortCriteria();
        appointmentSortCriteria.setSort("doctorDesc");

        //when
        List<AppointmentDto> result = appointmentService.sortAndFind(appointmentSortCriteria);

        //then
        verify(appointmentRepository).findAll();
        assertNotNull(result);
        assertEquals(appointmentList.size(), result.size());

        assertEquals("Oliesniak Karol", result.get(0).getDoctorLastName());
        assertEquals("Konieczko Mateusz", result.get(1).getDoctorLastName());
    }
}
