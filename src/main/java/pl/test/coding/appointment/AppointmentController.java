package pl.test.coding.appointment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.test.coding.appointment.model.AppointmentSortCriteria;
import pl.test.coding.appointment.model.dto.AppointmentDto;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.doctor.DoctorService;
import pl.test.coding.doctor.model.dto.DoctorDto;
import pl.test.coding.patient.PatientService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    public final AppointmentService appointmentService;
    public final DoctorService doctorService;
    public final PatientService patientService;

    @GetMapping(params = "reason")
    @ResponseBody
    public List<DoctorDto> getDoctorsByReason(@RequestParam("reason") ReasonForVisit reason) {
        return doctorService.findDoctorsByReason(reason);
    }

    @GetMapping
    public String getAll(AppointmentSortCriteria appointmentSortCriteria, Model model) {
        model.addAttribute("uniqueDoctorLastNames", doctorService.findUniqueDoctorLastNames());
        model.addAttribute("uniquePatientLastNames", patientService.findUniquePatientLastNames());
        model.addAttribute("appointments", appointmentService.sortAndFind(appointmentSortCriteria));
        return "appointment/list";
    }

    @GetMapping("/create")
    public String getCreateForm(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("today", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        model.addAttribute("reasonForVisit", ReasonForVisit.values());
        return "appointment/form";
    }

    @PostMapping("/create")
    public String create(AppointmentDto appointment, Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("today", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        model.addAttribute("reasonForVisit", ReasonForVisit.values());
        model.addAttribute("appointment", appointment);

        appointmentService.create(appointment);
        return "redirect:/appointments";
    }

    @DeleteMapping
    @ResponseBody
    public void deleteById(@RequestParam int idToDelete) {
        appointmentService.deleteById(idToDelete);
    }
}
