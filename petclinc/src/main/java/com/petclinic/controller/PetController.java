// --------- PetController.java ---------
package com.petclinic.controller;

import com.petclinic.dto.PetForm;
import com.petclinic.model.Owner;
import com.petclinic.model.Pet;
import com.petclinic.service.OwnerService;
import com.petclinic.service.PetService;
import com.petclinic.service.PetTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;
    private final OwnerService ownerService;
    private final PetTypeService petTypeService;

    private static final String PET_ATTR = "pet";
    private static final String PETS_ATTR = "pets";
    private static final String OWNERS_ATTR = "owners";
    private static final String PET_TYPES_ATTR = "petTypes";
    private static final String PETS_LIST_VIEW = "pets/list";
    private static final String PET_FORM_VIEW = "pets/form";
    private static final String REDIRECT_PETS = "redirect:/pets";
    private static final String REDIRECT_PET = "redirect:/pets/";

    @GetMapping
    public String list(Model model) {
        model.addAttribute(PETS_ATTR, petService.getAllPets());
        return PETS_LIST_VIEW;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Pet pet = petService.getPetById(id);
        model.addAttribute(PET_ATTR, pet);
        return "pets/detail";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute(PET_ATTR, new PetForm());
        model.addAttribute(OWNERS_ATTR, ownerService.getAllOwners());
        model.addAttribute(PET_TYPES_ATTR, petTypeService.getAllPetTypes());
        return PET_FORM_VIEW;
    }

    @PostMapping
    public String save(@Valid @ModelAttribute(PET_ATTR) PetForm petForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(OWNERS_ATTR, ownerService.getAllOwners());
            model.addAttribute(PET_TYPES_ATTR, petTypeService.getAllPetTypes());
            return PET_FORM_VIEW;
        }
        Pet savedPet = petService.createPet(toEntity(petForm));
        return REDIRECT_PET + savedPet.getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Pet pet = petService.getPetById(id);
        model.addAttribute(PET_ATTR, toForm(pet));
        model.addAttribute(OWNERS_ATTR, ownerService.getAllOwners());
        model.addAttribute(PET_TYPES_ATTR, petTypeService.getAllPetTypes());
        return PET_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute(PET_ATTR) PetForm petForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(OWNERS_ATTR, ownerService.getAllOwners());
            model.addAttribute(PET_TYPES_ATTR, petTypeService.getAllPetTypes());
            return PET_FORM_VIEW;
        }
        petService.updatePet(id, toEntity(petForm));
        return REDIRECT_PET + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        petService.deletePet(id);
        return REDIRECT_PETS;
    }

    @GetMapping("/owner/{ownerId}")
    public String listByOwner(@PathVariable Long ownerId, Model model) {
        Owner owner = ownerService.getOwnerById(ownerId);
        model.addAttribute(PETS_ATTR, petService.getPetsByOwner(owner));
        model.addAttribute("owner", owner);
        return PETS_LIST_VIEW;
    }

    private PetForm toForm(Pet pet) {
        PetForm form = new PetForm();
        form.setId(pet.getId());
        form.setName(pet.getName());
        form.setDateOfBirth(pet.getDateOfBirth());
        form.setPetTypeId(pet.getPetType() != null ? pet.getPetType().getId() : null);
        form.setOwnerId(pet.getOwner() != null ? pet.getOwner().getId() : null);
        return form;
    }

    private Pet toEntity(PetForm form) {
        Pet pet = new Pet();
        pet.setId(form.getId());
        pet.setName(form.getName());
        pet.setDateOfBirth(form.getDateOfBirth());
        pet.setPetType(form.getPetTypeId() != null ? petTypeService.getPetTypeById(form.getPetTypeId()) : null);
        pet.setOwner(form.getOwnerId() != null ? ownerService.getOwnerById(form.getOwnerId()) : null);
        return pet;
    }
}
