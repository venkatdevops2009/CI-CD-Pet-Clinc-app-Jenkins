// --------- VeterinarianController.java ---------
package com.petclinic.controller;

import com.petclinic.dto.VeterinarianForm;
import com.petclinic.model.Specialty;
import com.petclinic.model.Veterinarian;
import com.petclinic.service.SpecialtyService;
import com.petclinic.service.VeterinarianService;
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

    private static final String VET_ATTR = "vet";
    private static final String VETS_ATTR = "vets";
    private static final String SPECIALTIES_ATTR = "specialties";
    private static final String VETS_LIST_VIEW = "vets/list";
    private static final String VET_FORM_VIEW = "vets/form";
    private static final String REDIRECT_VETS = "redirect:/vets";
    private static final String REDIRECT_VET = "redirect:/vets/";

    @GetMapping
    public String list(Model model) {
        model.addAttribute(VETS_ATTR, veterinarianService.getAllVeterinarians());
        return VETS_LIST_VIEW;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Veterinarian vet = veterinarianService.getVeterinarianById(id);
        model.addAttribute(VET_ATTR, toForm(vet));
        return "vets/detail";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute(VET_ATTR, new VeterinarianForm());
        model.addAttribute(SPECIALTIES_ATTR, specialtyService.getAllSpecialties());
        return VET_FORM_VIEW;
    }

    @PostMapping
    public String save(@Valid @ModelAttribute(VET_ATTR) VeterinarianForm veterinarianForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(SPECIALTIES_ATTR, specialtyService.getAllSpecialties());
            return VET_FORM_VIEW;
        }
        Veterinarian savedVet = veterinarianService.createVeterinarian(toEntity(veterinarianForm));
        return REDIRECT_VET + savedVet.getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Veterinarian vet = veterinarianService.getVeterinarianById(id);
        model.addAttribute(VET_ATTR, toForm(vet));
        model.addAttribute(SPECIALTIES_ATTR, specialtyService.getAllSpecialties());
        return VET_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute(VET_ATTR) VeterinarianForm veterinarianForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(SPECIALTIES_ATTR, specialtyService.getAllSpecialties());
            return VET_FORM_VIEW;
        }
        veterinarianService.updateVeterinarian(id, toEntity(veterinarianForm));
        return REDIRECT_VET + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        veterinarianService.deleteVeterinarian(id);
        return REDIRECT_VETS;
    }

    private VeterinarianForm toForm(Veterinarian veterinarian) {
        VeterinarianForm form = new VeterinarianForm();
        form.setId(veterinarian.getId());
        form.setFirstName(veterinarian.getFirstName());
        form.setLastName(veterinarian.getLastName());
        form.setEmail(veterinarian.getEmail());
        form.setPhoneNumber(veterinarian.getPhoneNumber());
        form.setLicenseNumber(veterinarian.getLicenseNumber());
        form.setYearsOfExperience(veterinarian.getYearsOfExperience());
        form.setSpecialtyIds(veterinarian.getSpecialties() == null ? null : veterinarian.getSpecialties().stream()
                .map(Specialty::getId)
                .toList());
        return form;
    }

    private Veterinarian toEntity(VeterinarianForm form) {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setId(form.getId());
        veterinarian.setFirstName(form.getFirstName());
        veterinarian.setLastName(form.getLastName());
        veterinarian.setEmail(form.getEmail());
        veterinarian.setPhoneNumber(form.getPhoneNumber());
        veterinarian.setLicenseNumber(form.getLicenseNumber());
        veterinarian.setYearsOfExperience(form.getYearsOfExperience());
        return veterinarian;
    }
}
