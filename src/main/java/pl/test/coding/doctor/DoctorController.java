package pl.test.coding.doctor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.test.coding.common.ReasonForVisit;
import pl.test.coding.common.Specialization;
import pl.test.coding.doctor.model.Doctor;
import pl.test.coding.doctor.model.dto.DoctorDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    public final DoctorService doctorService;

    @GetMapping
    public String getAll(Model model) {
        List<Doctor> doctors = doctorService.findAll();
        model.addAttribute("doctors", doctors);
        return "doctor/list";
    }

    @GetMapping("/create")
    public String getCreateForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("specializations", Specialization.values());
        return "doctor/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("doctor") Doctor doctor, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctor", doctor);
            model.addAttribute("errorMessages", bindingResult.getAllErrors());
            return "doctor/form";
        }
        doctorService.create(doctor);
        return "redirect:/doctors";
    }

    @GetMapping(params = "reason")
    @ResponseBody
    public List<DoctorDto> getDoctorsByReason(@RequestParam("reason") ReasonForVisit reason) {
        return doctorService.findDoctorsByReason(reason);
    }

    @DeleteMapping
    @ResponseBody
    public void deleteById(@RequestParam int id) {
        doctorService.deleteById(id);
    }
}
