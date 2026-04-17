package com.lithoapp.patientservice.entity;

import com.lithoapp.patientservice.enums.KidneyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "clinical_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    @Column(name = "family_history")
    private Boolean familyHistory;

    @Column(name = "personal_history")
    private Boolean personalHistory;

    @Column(name = "last_episode_date")
    private LocalDate lastEpisodeDate;

    @Column(name = "lithiasis_type", length = 100)
    private String lithiasisType;

    @Column(name = "frequent_infections")
    private Boolean frequentInfections;

    @Column(name = "single_kidney")
    private Boolean singleKidney;

    @Enumerated(EnumType.STRING)
    @Column(name = "kidney_type", columnDefinition = "VARCHAR(20)")
    private KidneyType kidneyType;

    @Column(name = "chronic_renal_failure")
    private Boolean chronicRenalFailure;

    @Column(name = "clearance")
    private Double clearance;
}
