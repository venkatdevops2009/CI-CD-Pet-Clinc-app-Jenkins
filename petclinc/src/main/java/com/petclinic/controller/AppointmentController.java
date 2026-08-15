// --------- AppointmentController.java ---------
package com.petclinic.controller;

import com.petclinic.model.Appointment;
import com.petclinic.service.AppointmentService;
import com.petclinic.service.PetService;
import com.petclinic.service.VeterinarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final PetService petService;
    private final VeterinarianService veterinarianService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "appointments/list";
    }

    @GetMapping("/upcoming")
    public String upcoming(Model model) {
        model.addAttribute("appointments", appointmentService.getUpcomingAppointments());
        return "appointments/list";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("pets", petService.getAllPets());
        model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
        return "appointments/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Appointment appointment, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pets", petService.getAllPets());
            model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
            return "appointments/form";
        }
        Appointment saved = appointmentService.createAppointment(appointment);
        return "redirect:/appointments";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        model.addAttribute("appointment", appointment);
        model.addAttribute("pets", petService.getAllPets());
        model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
        return "appointments/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Appointment appointment, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pets", petService.getAllPets());
            model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
            return "appointments/form";
        }
        appointmentService.updateAppointment(id, appointment);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        appointmentService.updateStatus(id, status);
        return "redirect:/appointments";
    }
   }
