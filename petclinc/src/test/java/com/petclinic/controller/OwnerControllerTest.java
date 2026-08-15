package com.petclinic.controller;

import com.petclinic.model.Owner;
import com.petclinic.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OwnerController.class)
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

    @Test
    void listShowsOwners() throws Exception {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setFirstName("Alice");
        owner.setLastName("Brown");
        owner.setAddress("One Street");
        owner.setCity("Seattle");
        owner.setTelephone("1234567890");

        when(ownerService.getAllOwners()).thenReturn(List.of(owner));

        mockMvc.perform(get("/owners"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/list"))
            .andExpect(model().attributeExists("owners"));
    }

    @Test
    void saveRedirectsWhenValid() throws Exception {
        mockMvc.perform(post("/owners")
                .param("firstName", "Alice")
                .param("lastName", "Brown")
                .param("address", "One Street")
                .param("city", "Seattle")
                .param("telephone", "1234567890"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/owners"));
    }

    @Test
    void saveReturnsFormWhenValidationFails() throws Exception {
        mockMvc.perform(post("/owners")
                .param("firstName", "")
                .param("lastName", "Brown")
                .param("address", "One Street")
                .param("city", "Seattle")
                .param("telephone", "1234567890"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/form"));
    }
}
