package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafetyNetGrievanceSummaryRequest {
    Date fromDate;
    Date toDate;
    String languageCode;
    String spProgrammeId;
    String division;
    String district;
    String upazila;
}