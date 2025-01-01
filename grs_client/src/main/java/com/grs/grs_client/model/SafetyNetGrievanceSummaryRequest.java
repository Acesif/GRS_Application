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
    private Date fromDate;
    private Date toDate;
    private String languageCode;
    private String spProgrammeId;
    private String division;
    private String district;
    private String upazila;
}