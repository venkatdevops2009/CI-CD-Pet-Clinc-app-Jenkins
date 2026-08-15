package com.petclinic.controller;

import com.petclinic.service.AppointmentService;
import com.petclinic.service.OwnerService;
import com.petclinic.service.PetService;
import com.petclinic.service.VeterinarianService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private PetService petService;

    @MockBean
    private VeterinarianService veterinarianService;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    void indexLoadsDashboardData() throws Exception {
        when(ownerService.getAllOwners()).thenReturn(Collections.emptyList());
        when(petService.getAllPets()).thenReturn(Collections.emptyList());
        when(veterinarianService.getAllVeterinarians()).thenReturn(Collections.emptyList());
        when(appointmentService.getUpcomingAppointments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("totalOwners", "totalPets", "totalVets", "upcomingAppointments"));
    }

    @Test
    void dashboardRedirectsToIndexView() throws Exception {
        when(ownerService.getAllOwners()).thenReturn(Collections.emptyList());
        when(petService.getAllPets()).thenReturn(Collections.emptyList());
        when(veterinarianService.getAllVeterinarians()).thenReturn(Collections.emptyList());
        when(appointmentService.getUpcomingAppointments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }
}
