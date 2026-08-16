// --------- AppointmentController.java ---------
package com.petclinic.controller;

import com.petclinic.dto.AppointmentForm;
import com.petclinic.model.Appointment;
import com.petclinic.service.AppointmentService;
import com.petclinic.service.PetService;
import com.petclinic.service.VeterinarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
   private static final String APPOINTMENT_FORM_VIEW = "appointments/form";
   private static final String REDIRECT_APPOINTMENTS = "redirect:/appointments";
   private static final String VETERINARIANS_ATTR = "veterinarians";
   private static final String PETS_ATTR = "pets";

   private final AppointmentService appointmentService;
   private final PetService petService;
   private final VeterinarianService veterinarianService;

   @GetMapping
   public String list(Model model) {
       model.addAttribute("appointments", appointmentService.getAllAppointments());
       return "appointments/list";
   }

   @GetMapping("/upcoming")
   public String upcoming(Model model) {
       model.addAttribute("appointments", appointmentService.getUpcomingAppointments());
       return "appointments/list";
   }

   @GetMapping("/new")
   public String create(Model model) {
       model.addAttribute("appointment", new AppointmentForm());
       model.addAttribute(PETS_ATTR, petService.getAllPets());
       model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
       return APPOINTMENT_FORM_VIEW;
   }

   @PostMapping
   public String save(@Valid @ModelAttribute("appointment") AppointmentForm appointmentForm, BindingResult result, Model model) {
       if (result.hasErrors()) {
           model.addAttribute(PETS_ATTR, petService.getAllPets());
           model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
           return APPOINTMENT_FORM_VIEW;
       }
       appointmentService.createAppointment(toEntity(appointmentForm));
       return REDIRECT_APPOINTMENTS;
   }

   @GetMapping("/{id}")
   public String detail(@PathVariable Long id, Model model) {
       Appointment appointment = appointmentService.getAppointmentById(id);
       model.addAttribute("appointment", appointment);
       return "appointments/detail";
   }

   @GetMapping("/{id}/edit")
   public String edit(@PathVariable Long id, Model model) {
       Appointment appointment = appointmentService.getAppointmentById(id);
       model.addAttribute("appointment", toForm(appointment));
       model.addAttribute(PETS_ATTR, petService.getAllPets());
       model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
       return APPOINTMENT_FORM_VIEW;
   }

   @PostMapping("/{id}")
   public String update(@PathVariable Long id, @Valid @ModelAttribute("appointment") AppointmentForm appointmentForm, BindingResult result, Model model) {
       if (result.hasErrors()) {
           model.addAttribute(PETS_ATTR, petService.getAllPets());
           model.addAttribute(VETERINARIANS_ATTR, veterinarianService.getAllVeterinarians());
           return APPOINTMENT_FORM_VIEW;
       }
       appointmentService.updateAppointment(id, toEntity(appointmentForm));
       return REDIRECT_APPOINTMENTS;
   }

   @PostMapping("/{id}/delete")
   public String delete(@PathVariable Long id) {
       appointmentService.deleteAppointment(id);
       return REDIRECT_APPOINTMENTS;
   }

   @PostMapping("/{id}/status")
   public String updateStatus(@PathVariable Long id, @RequestParam String status) {
       appointmentService.updateStatus(id, status);
       return REDIRECT_APPOINTMENTS;
   }

   private AppointmentForm toForm(Appointment appointment) {
       AppointmentForm form = new AppointmentForm();
       form.setId(appointment.getId());
       form.setPetId(appointment.getPet() != null ? appointment.getPet().getId() : null);
       form.setVeterinarianId(appointment.getVeterinarian() != null ? appointment.getVeterinarian().getId() : null);
       form.setAppointmentTime(appointment.getAppointmentTime());
       form.setReason(appointment.getReason());
       form.setStatus(appointment.getStatus());
       form.setNotes(appointment.getNotes());
       return form;
   }

   private Appointment toEntity(AppointmentForm form) {
       Appointment appointment = new Appointment();
       if (form.getId() != null) appointment.setId(form.getId());
       if (form.getPetId() != null) appointment.setPet(petService.getPetById(form.getPetId()));
       if (form.getVeterinarianId() != null) appointment.setVeterinarian(veterinarianService.getVeterinarianById(form.getVeterinarianId()));
       appointment.setAppointmentTime(form.getAppointmentTime());
       appointment.setReason(form.getReason());
       appointment.setStatus(form.getStatus());
       appointment.setNotes(form.getNotes());
       return appointment;
   }
}
