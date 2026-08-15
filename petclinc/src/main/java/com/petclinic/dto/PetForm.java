package com.petclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Pet name is required")
    private String name;

    @NotNull(message = "Pet type is required")
    private Long petTypeId;

    @NotNull(message = "Owner is required")
    private Long ownerId;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    private String breed;
    private String color;
}
