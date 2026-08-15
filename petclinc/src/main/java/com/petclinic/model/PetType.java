// --------- PetType.java ---------
package com.petclinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pet_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pet type name is required")
    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}

