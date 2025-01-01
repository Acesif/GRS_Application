package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SafetyNetGrievanceSummaryListDto {
    private List<SafetyNetGrievanceSummaryDto> safetyNetGrievanceSummaryList;
    private Integer recordCount;
}
