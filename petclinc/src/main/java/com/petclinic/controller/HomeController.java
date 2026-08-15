// --------- HomeController.java ---------
package com.petclinic.controller;

import com.petclinic.service.OwnerService;
import com.petclinic.service.PetService;
import com.petclinic.service.VeterinarianService;
import com.petclinic.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final OwnerService ownerService;
    private final PetService petService;
    private final VeterinarianService veterinarianService;
    private final AppointmentService appointmentService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("totalOwners", ownerService.getAllOwners().size());
        model.addAttribute("totalPets", petService.getAllPets().size());
        model.addAttribute("totalVets", veterinarianService.getAllVeterinarians().size());
        model.addAttribute("upcomingAppointments", appointmentService.getUpcomingAppointments().size());
        return "index";
    }

    @GetMapping("dashboard")
    public String dashboard(Model model) {
        return index(model);
    }
}
