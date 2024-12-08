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
    public String spProgramName;
    public String divisionName;
    public String districtName;
    public String upazilaName;
    public Integer totalGrievances;
    public Integer resolvedGrievances;
    public Integer outstandingGrievances;
}
