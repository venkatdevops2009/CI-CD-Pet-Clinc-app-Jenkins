package com.petclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetForm {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private Long petTypeId;
    private Long ownerId;
}
