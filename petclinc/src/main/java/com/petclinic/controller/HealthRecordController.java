// --------- HealthRecordController.java ---------
package com.petclinic.controller;

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
        HealthRecord record = new HealthRecord();
        if (petId != null) {
            record.setPet(petService.getPetById(petId));
        }
        model.addAttribute("record", record);
        model.addAttribute("pets", petService.getAllPets());
        model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
        return "health-records/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute HealthRecord record, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pets", petService.getAllPets());
            model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
            return "health-records/form";
        }
        HealthRecord savedRecord = healthRecordService.createHealthRecord(record);
        return "redirect:/health-records/pet/" + savedRecord.getPet().getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        HealthRecord record = healthRecordService.getHealthRecordById(id);
        model.addAttribute("record", record);
        model.addAttribute("pets", petService.getAllPets());
        model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
        return "health-records/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute HealthRecord record, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pets", petService.getAllPets());
            model.addAttribute("veterinarians", veterinarianService.getAllVeterinarians());
            return "health-records/form";
        }
        HealthRecord updated = healthRecordService.updateHealthRecord(id, record);
        return "redirect:/health-records/pet/" + updated.getPet().getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        HealthRecord record = healthRecordService.getHealthRecordById(id);
        Long petId = record.getPet().getId();
        healthRecordService.deleteHealthRecord(id);
        return "redirect:/health-records/pet/" + petId;
    }
}
