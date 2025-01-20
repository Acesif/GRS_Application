package com.grs.mobileApp.dto;


import com.grs.core.domain.GrievanceCurrentStatus;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileInvestigationForwardingDTO {

    private Long Complaint_id;
    private Long office_id;
    private Long username;
    private String note;
    private Long to_employee_record_id;
    private GrievanceCurrentStatus currentStatus = GrievanceCurrentStatus.STATEMENT_ANSWERED;
    private String officers;

}




