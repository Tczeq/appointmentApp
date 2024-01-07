package pl.test.coding.appointment;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.test.coding.appointment.model.dto.AppointmentDto;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.doctor.DoctorService;
import pl.test.coding.patient.PatientService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    public final AppointmentService appointmentService;
    public final DoctorService doctorService;
    public final PatientService patientService;

    @GetMapping
    public String getAll(@RequestParam(required = false) String reasonForVisit,
                         @RequestParam(required = false) String doctorLastName,
                         @RequestParam(required = false) String patientLastName,
                         @RequestParam(required = false) Integer appointmentTime,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate,
                         @RequestParam(required = false) String sort,
                         Model model) {

        Set<String> uniqueDoctorLastNames = doctorService.findUniqueDoctorLastNames();
        Set<String> uniquePatientLastNames = patientService.findUniquePatientLastNames();
        model.addAttribute("uniqueDoctorLastNames", uniqueDoctorLastNames);
        model.addAttribute("uniquePatientLastNames", uniquePatientLastNames);

        List<AppointmentDto> appointments = appointmentService.sortAndFind(reasonForVisit, doctorLastName, patientLastName, startDate, endDate, appointmentTime, sort);

        model.addAttribute("appointments", appointments);
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
    public String create(AppointmentDto appointment, BindingResult bindingResult, Model model) {
        LocalDateTime now = LocalDateTime.now();
        if (appointment.getTerm().isBefore(now)) {
            model.addAttribute("errorMessages", bindingResult.getAllErrors());
            bindingResult.rejectValue("term", "error.appointment", "The appointment date cannot be from the past.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("today", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            model.addAttribute("reasonForVisit", ReasonForVisit.values());
            model.addAttribute("appointment", appointment);
            return "appointment/form";
        }

        appointment.setIsDeleted(false);
        appointmentService.create(appointment);
        return "redirect:/appointments";
    }

    @DeleteMapping
    @ResponseBody
    public void deleteById(@RequestParam int idToDelete) {
        appointmentService.deleteById(idToDelete);
    }
}
