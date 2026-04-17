package com.lithoapp.patientservice.dto.response;

import com.lithoapp.patientservice.enums.KidneyType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClinicalInfoResponse {

    private Long id;
    private Boolean familyHistory;
    private Boolean personalHistory;
    private LocalDate lastEpisodeDate;
    private String lithiasisType;
    private Boolean frequentInfections;
    private Boolean singleKidney;
    private KidneyType kidneyType;
    private Boolean chronicRenalFailure;
    private Double clearance;
}
