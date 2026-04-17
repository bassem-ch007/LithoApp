package com.lithoapp.patientservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "anatomical_anomalies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnatomicalAnomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "name", nullable = false, length = 150)
    private String name;
}
