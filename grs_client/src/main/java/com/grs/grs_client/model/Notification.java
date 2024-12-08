package com.grs.grs_client.model;

import lombok.Data;

import java.util.Date;

@Data
public class Notification {
    private Long id;
    private String text;
    private String employeeNameEng;
    private String employeeNameBng;
    private Long officeId;
    private Long employeeRecordId;
    private Long officeUnitOrganogramId;
    private Long complaintId;
    private GrievanceForwarding grievanceForwarding;
    private Boolean isSeen;
    private String url;
    private Date createdAt;
    private Date updatedAt;
    private Long createdBy;
    private Long modifiedBy;
    private Boolean status;
}
