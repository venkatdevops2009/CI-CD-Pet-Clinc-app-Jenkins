// --------- OwnerController.java ---------
package com.petclinic.controller;

import com.petclinic.model.Owner;
import com.petclinic.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("owners", ownerService.getAllOwners());
        return "owners/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Owner owner = ownerService.getOwnerById(id);
        model.addAttribute("owner", owner);
        return "owners/detail";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("owner", new Owner());
        return "owners/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Owner owner, BindingResult result) {
        if (result.hasErrors()) {
            return "owners/form";
        }
        ownerService.createOwner(owner);
        return "redirect:/owners";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Owner owner = ownerService.getOwnerById(id);
        model.addAttribute("owner", owner);
        return "owners/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Owner owner, BindingResult result) {
        if (result.hasErrors()) {
            return "owners/form";
        }
        ownerService.updateOwner(id, owner);
        return "redirect:/owners/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return "redirect:/owners";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String firstName, 
                         @RequestParam(required = false) String lastName, 
                         Model model) {
        if ((firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName.isEmpty())) {
            model.addAttribute("owners", ownerService.searchByName(firstName, lastName));
        }
        return "owners/list";
    }
}
