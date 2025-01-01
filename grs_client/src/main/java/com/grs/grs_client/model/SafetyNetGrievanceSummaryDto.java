package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SafetyNetGrievanceSummaryDto {
    private String spProgramName;
    private String divisionName;
    private String districtName;
    private String upazilaName;
    private Integer totalGrievances;
    private Integer resolvedGrievances;
    private Integer outstandingGrievances;
}
