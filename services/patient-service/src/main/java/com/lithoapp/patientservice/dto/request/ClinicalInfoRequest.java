package com.lithoapp.patientservice.dto.request;

import com.lithoapp.patientservice.enums.KidneyType;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClinicalInfoRequest {

    private Boolean familyHistory;
    private Boolean personalHistory;
    private LocalDate lastEpisodeDate;
    private String lithiasisType;
    private Boolean frequentInfections;
    private Boolean singleKidney;
    private KidneyType kidneyType;
    private Boolean chronicRenalFailure;

    @Positive(message = "Clearance must be a positive value")
    private Double clearance;
}
