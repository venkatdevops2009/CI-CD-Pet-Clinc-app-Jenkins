// --------- PetController.java ---------
package com.petclinic.controller;

import com.petclinic.model.Pet;
import com.petclinic.model.Owner;
import com.petclinic.service.PetService;
import com.petclinic.service.OwnerService;
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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pets", petService.getAllPets());
        return "pets/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Pet pet = petService.getPetById(id);
        model.addAttribute("pet", pet);
        return "pets/detail";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("pet", new Pet());
        model.addAttribute("owners", ownerService.getAllOwners());
        model.addAttribute("petTypes", petTypeService.getAllPetTypes());
        return "pets/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Pet pet, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("owners", ownerService.getAllOwners());
            model.addAttribute("petTypes", petTypeService.getAllPetTypes());
            return "pets/form";
        }
        Pet savedPet = petService.createPet(pet);
        return "redirect:/pets/" + savedPet.getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Pet pet = petService.getPetById(id);
        model.addAttribute("pet", pet);
        model.addAttribute("owners", ownerService.getAllOwners());
        model.addAttribute("petTypes", petTypeService.getAllPetTypes());
        return "pets/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Pet pet, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("owners", ownerService.getAllOwners());
            model.addAttribute("petTypes", petTypeService.getAllPetTypes());
            return "pets/form";
        }
        petService.updatePet(id, pet);
        return "redirect:/pets/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        petService.deletePet(id);
        return "redirect:/pets";
    }

    @GetMapping("/owner/{ownerId}")
    public String listByOwner(@PathVariable Long ownerId, Model model) {
        Owner owner = ownerService.getOwnerById(ownerId);
        model.addAttribute("pets", petService.getPetsByOwner(owner));
        model.addAttribute("owner", owner);
        return "pets/list";
    }
}
