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
    private static final String RECORD_ATTR = "record";
    private static final String RECORDS_ATTR = "records";
    private static final String PET_ATTR = "pet";
    private static final String REDIRECT_TO_PET = "redirect:/health-records/pet/";

    private final HealthRecordService healthRecordService;
    private final PetService petService;
    private final VeterinarianService veterinarianService;

    private static final String HEALTH_RECORDS_LIST_VIEW = "health-records/list";

    @GetMapping
    public String list(Model model) {
        model.addAttribute(RECORDS_ATTR, healthRecordService.getAllHealthRecords());
        return HEALTH_RECORDS_LIST_VIEW;
    }

    @GetMapping("/pet/{petId}")
    public String listByPet(@PathVariable Long petId, Model model) {
        Pet pet = petService.getPetById(petId);
        model.addAttribute(RECORDS_ATTR, healthRecordService.getHealthRecordsByPet(pet));
        model.addAttribute(PET_ATTR, pet);
        return HEALTH_RECORDS_LIST_VIEW;
    }

    @GetMapping("/new")
    public String create(@RequestParam(required = false) Long petId, Model model) {
        HealthRecordForm healthRecordForm = new HealthRecordForm();
        if (petId != null) {
            healthRecordForm.setPetId(petId);
        }
        model.addAttribute(RECORD_ATTR, healthRecordForm);
        model.addAttribute(PETS_ATTR, petService.getAllPets());
        model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
        return HEALTH_RECORD_FORM_VIEW;
    }

    @PostMapping
    public String save(@Valid @ModelAttribute(RECORD_ATTR) HealthRecordForm healthRecordForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(PETS_ATTR, petService.getAllPets());
            model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
            return HEALTH_RECORD_FORM_VIEW;
        }
        HealthRecord savedRecord = healthRecordService.createHealthRecord(toEntity(healthRecordForm));
        return REDIRECT_TO_PET + savedRecord.getPet().getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        HealthRecord healthRecord = healthRecordService.getHealthRecordById(id);
        model.addAttribute(RECORD_ATTR, toForm(healthRecord));
        model.addAttribute(PETS_ATTR, petService.getAllPets());
        model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
        return HEALTH_RECORD_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute(RECORD_ATTR) HealthRecordForm healthRecordForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(PETS_ATTR, petService.getAllPets());
            model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
            return HEALTH_RECORD_FORM_VIEW;
        }
        HealthRecord updated = healthRecordService.updateHealthRecord(id, toEntity(healthRecordForm));
        return REDIRECT_TO_PET + updated.getPet().getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        HealthRecord healthRecord = healthRecordService.getHealthRecordById(id);
        Long petId = healthRecord.getPet().getId();
        healthRecordService.deleteHealthRecord(id);
        return REDIRECT_TO_PET + petId;
    }

    private HealthRecordForm toForm(HealthRecord healthRecord) {
        HealthRecordForm form = new HealthRecordForm();
        if (healthRecord.getPet() != null) form.setPetId(healthRecord.getPet().getId());
        form.setRecordType(healthRecord.getRecordType());
        form.setRecordDate(healthRecord.getRecordDate());
        form.setDescription(healthRecord.getDescription());
        form.setMedication(healthRecord.getMedication());
        form.setDosage(healthRecord.getDosage());
        form.setDiagnosis(healthRecord.getDiagnosis());
        form.setTreatment(healthRecord.getTreatment());
        form.setNotes(healthRecord.getNotes());
        if (healthRecord.getVeterinarian() != null) form.setVeterinarianId(healthRecord.getVeterinarian().getId());
        return form;
    }

    private HealthRecord toEntity(HealthRecordForm form) {
        HealthRecord healthRecord = new HealthRecord();
        if (form.getPetId() != null) healthRecord.setPet(petService.getPetById(form.getPetId()));
        healthRecord.setRecordType(form.getRecordType());
        healthRecord.setRecordDate(form.getRecordDate());
        healthRecord.setDescription(form.getDescription());
        healthRecord.setMedication(form.getMedication());
        healthRecord.setDosage(form.getDosage());
        healthRecord.setDiagnosis(form.getDiagnosis());
        healthRecord.setTreatment(form.getTreatment());
        healthRecord.setNotes(form.getNotes());
        if (form.getVeterinarianId() != null) healthRecord.setVeterinarian(veterinarianService.getVeterinarianById(form.getVeterinarianId()));
        return healthRecord;
    }
}
