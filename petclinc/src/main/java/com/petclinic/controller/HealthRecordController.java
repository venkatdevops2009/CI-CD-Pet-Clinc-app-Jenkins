// --------- HealthRecordController.java ---------
package com.petclinic.controller;

import com.petclinic.dto.HealthRecordForm;
import com.petclinic.model.HealthRecord;
import com.petclinic.model.Pet;
import com.petclinic.service.HealthRecordService;
import com.petclinic.service.PetService;
import com.petclinic.service.VeterinarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/health-records")
@RequiredArgsConstructor
public class HealthRecordController {
    private static final String HEALTH_RECORD_FORM_VIEW = "health-records/form";
    private static final String PETS_ATTR = "pets";
    private static final String VETERINARIANS_ATTR = "veterinarians";

    private final HealthRecordService healthRecordService;
    private final PetService petService;
    private final VeterinarianService veterinarianService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("records", healthRecordService.getAllHealthRecords());
        return "health-records/list";
    }

    @GetMapping("/pet/{petId}")
    public String listByPet(@PathVariable Long petId, Model model) {
        Pet pet = petService.getPetById(petId);
        model.addAttribute("records", healthRecordService.getHealthRecordsByPet(pet));
        model.addAttribute("pet", pet);
        return "health-records/list";
    }

    @GetMapping("/new")
    public String create(@RequestParam(required = false) Long petId, Model model) {
        HealthRecordForm healthRecordForm = new HealthRecordForm();
        if (petId != null) {
            healthRecordForm.setPet(petService.getPetById(petId));
        }
        model.addAttribute("record", healthRecordForm);
        model.addAttribute(PETS_ATTR, petService.getAllPets());
        model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
        return HEALTH_RECORD_FORM_VIEW;
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("record") HealthRecordForm healthRecordForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(PETS_ATTR, petService.getAllPets());
            model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
            return HEALTH_RECORD_FORM_VIEW;
        }
        HealthRecord savedRecord = healthRecordService.createHealthRecord(toEntity(healthRecordForm));
        return "redirect:/health-records/pet/" + savedRecord.getPet().getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        HealthRecord healthRecord = healthRecordService.getHealthRecordById(id);
        model.addAttribute("record", toForm(healthRecord));
        model.addAttribute(PETS_ATTR, petService.getAllPets());
        model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
        return HEALTH_RECORD_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("record") HealthRecordForm healthRecordForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(PETS_ATTR, petService.getAllPets());
            model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
            return HEALTH_RECORD_FORM_VIEW;
        }
        HealthRecord updated = healthRecordService.updateHealthRecord(id, toEntity(healthRecordForm));
        return "redirect:/health-records/pet/" + updated.getPet().getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        HealthRecord healthRecord = healthRecordService.getHealthRecordById(id);
        Long petId = healthRecord.getPet().getId();
        healthRecordService.deleteHealthRecord(id);
        return "redirect:/health-records/pet/" + petId;
    }

    private HealthRecordForm toForm(HealthRecord healthRecord) {
        HealthRecordForm form = new HealthRecordForm();
        form.setPet(healthRecord.getPet());
        form.setRecordType(healthRecord.getRecordType());
        form.setRecordDate(healthRecord.getRecordDate());
        form.setDescription(healthRecord.getDescription());
        form.setMedication(healthRecord.getMedication());
        form.setDosage(healthRecord.getDosage());
        form.setDiagnosis(healthRecord.getDiagnosis());
        form.setTreatment(healthRecord.getTreatment());
        form.setNotes(healthRecord.getNotes());
        form.setVeterinarian(healthRecord.getVeterinarian());
        return form;
    }

    private HealthRecord toEntity(HealthRecordForm form) {
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setPet(form.getPet());
        healthRecord.setRecordType(form.getRecordType());
        healthRecord.setRecordDate(form.getRecordDate());
        healthRecord.setDescription(form.getDescription());
        healthRecord.setMedication(form.getMedication());
        healthRecord.setDosage(form.getDosage());
        healthRecord.setDiagnosis(form.getDiagnosis());
        healthRecord.setTreatment(form.getTreatment());
        healthRecord.setNotes(form.getNotes());
        healthRecord.setVeterinarian(form.getVeterinarian());
        return healthRecord;
    }
}
