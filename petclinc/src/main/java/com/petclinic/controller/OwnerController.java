// --------- OwnerController.java ---------
package com.petclinic.controller;

import com.petclinic.dto.OwnerForm;
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

    private static final String OWNER_ATTR = "owner";
    private static final String OWNERS_ATTR = "owners";
    private static final String OWNERS_LIST_VIEW = "owners/list";
    private static final String OWNER_FORM_VIEW = "owners/form";
    private static final String REDIRECT_OWNERS = "redirect:/owners";
    private static final String REDIRECT_OWNER = "redirect:/owners/";

    @GetMapping
    public String list(Model model) {
        model.addAttribute(OWNERS_ATTR, ownerService.getAllOwners());
        return OWNERS_LIST_VIEW;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Owner owner = ownerService.getOwnerById(id);
        model.addAttribute(OWNER_ATTR, owner);
        return "owners/detail";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute(OWNER_ATTR, new OwnerForm());
        return OWNER_FORM_VIEW;
    }

    @PostMapping
    public String save(@Valid @ModelAttribute(OWNER_ATTR) OwnerForm ownerForm, BindingResult result) {
        if (result.hasErrors()) {
            return OWNER_FORM_VIEW;
        }
        ownerService.createOwner(toEntity(ownerForm));
        return REDIRECT_OWNERS;
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Owner owner = ownerService.getOwnerById(id);
        model.addAttribute(OWNER_ATTR, toForm(owner));
        return OWNER_FORM_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute(OWNER_ATTR) OwnerForm ownerForm, BindingResult result) {
        if (result.hasErrors()) {
            return OWNER_FORM_VIEW;
        }
        ownerService.updateOwner(id, toEntity(ownerForm));
        return REDIRECT_OWNER + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return REDIRECT_OWNERS;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String firstName,
                         @RequestParam(required = false) String lastName,
                         Model model) {
        if ((firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName.isEmpty())) {
            model.addAttribute(OWNERS_ATTR, ownerService.searchByName(firstName, lastName));
        }
        return OWNERS_LIST_VIEW;
    }

    private OwnerForm toForm(Owner owner) {
        OwnerForm form = new OwnerForm();
        form.setId(owner.getId());
        form.setFirstName(owner.getFirstName());
        form.setLastName(owner.getLastName());
        form.setAddress(owner.getAddress());
        form.setCity(owner.getCity());
        form.setTelephone(owner.getTelephone());
        return form;
    }

    private Owner toEntity(OwnerForm form) {
        Owner owner = new Owner();
        owner.setId(form.getId());
        owner.setFirstName(form.getFirstName());
        owner.setLastName(form.getLastName());
        owner.setAddress(form.getAddress());
        owner.setCity(form.getCity());
        owner.setTelephone(form.getTelephone());
        return owner;
    }
}
