// --------- HealthRecord.java ---------
package com.petclinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pet is required")
    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @NotBlank(message = "Record type is required")
    @Column(nullable = false)
    private String recordType;

    @NotNull(message = "Record date is required")
    @Column(nullable = false)
    private LocalDate recordDate;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 1000)
    private String description;

    private String medication;
    private String dosage;
    private String diagnosis;
    private String treatment;
    private String notes;

    @NotNull(message = "Veterinarian is required")
    @ManyToOne
    @JoinColumn(name = "vet_id", nullable = false)
    private Veterinarian veterinarian;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
