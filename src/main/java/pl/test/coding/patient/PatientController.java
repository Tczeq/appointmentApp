package pl.test.coding.patient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.test.coding.patient.model.Patient;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {
    public final PatientService patientService;


    @GetMapping
    public String getAll(Model model) {
        List<Patient> patients = patientService.findAll();
        model.addAttribute("patients", patients);
        return "patient/list";
    }

    @GetMapping("/create")
    public String getCreateForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patient", patient);
            model.addAttribute("errorMessages", bindingResult.getAllErrors());
            return "patient/form";
        }
        patientService.create(patient);
        return "redirect:/patients";
    }


}
