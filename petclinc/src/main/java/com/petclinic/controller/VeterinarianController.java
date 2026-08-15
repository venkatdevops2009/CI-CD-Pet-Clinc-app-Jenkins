// --------- VeterinarianController.java ---------
package com.petclinic.controller;

import com.petclinic.model.Veterinarian;
import com.petclinic.service.VeterinarianService;
import com.petclinic.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vets")
@RequiredArgsConstructor
public class VeterinarianController {
    private final VeterinarianService veterinarianService;
    private final SpecialtyService specialtyService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vets", veterinarianService.getAllVeterinarians());
        return "vets/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Veterinarian vet = veterinarianService.getVeterinarianById(id);
        model.addAttribute("vet", vet);
        return "vets/detail";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("vet", new Veterinarian());
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        return "vets/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Veterinarian vet, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("vet", vet);
	    model.addAttribute("specialties", specialtyService.getAllSpecialties());
            return "vets/form";
        }
        Veterinarian savedVet = veterinarianService.createVeterinarian(vet);
        return "redirect:/vets/" + savedVet.getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Veterinarian vet = veterinarianService.getVeterinarianById(id);
        model.addAttribute("vet", vet);
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        return "vets/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Veterinarian vet, BindingResult result, Model model) {
        if (result.hasErrors()) {
	    model.addAttribute("vet", vet);
            model.addAttribute("specialties", specialtyService.getAllSpecialties());
            return "vets/form";
        }
        veterinarianService.updateVeterinarian(id, vet);
        return "redirect:/vets/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        veterinarianService.deleteVeterinarian(id);
        return "redirect:/vets";
    }
}

