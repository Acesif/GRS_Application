package com.grs.grs_client.model;

import com.grs.grs_client.enums.GrievanceCurrentStatus;
import com.grs.grs_client.enums.RoleType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GrievanceForwarding {
    private Long id;
    private Grievance grievance;
    private String comment;
    private String action;
    private Long toEmployeeRecordId;
    private Long fromEmployeeRecordId;
    private Long toOfficeUnitOrganogramId;
    private Long fromOfficeUnitOrganogramId;
    private Long toOfficeId;
    private Long fromOfficeId;
    private Long toOfficeUnitId;
    private Long fromOfficeUnitId;
    private GrievanceCurrentStatus currentStatus;
    private Date deadlineDate;
    private Boolean isCurrent;
    private Boolean isCC;
    private Boolean isCommitteeMember;
    private Boolean isCommitteeHead;
    private Boolean isSeen;
    private String toEmployeeNameBangla;
    private String fromEmployeeNameBangla;
    private String toEmployeeNameEnglish;
    private String fromEmployeeNameEnglish;
    private String toEmployeeDesignationBangla;
    private String fromEmployeeDesignationBangla;
    private String toOfficeNameBangla;
    private String fromOfficeNameBangla;
    private String toEmployeeUnitNameBangla;
    private String fromEmployeeUnitNameBangla;
    private String fromEmployeeUsername;
    private RoleType assignedRole;
    private String officeLayers;
    private Date createdAt;
    private Date updatedAt;
    private Long createdBy;
    private Long modifiedBy;
    private Boolean status;
    private List<MovementAttachedFile> attachedFiles;
}
