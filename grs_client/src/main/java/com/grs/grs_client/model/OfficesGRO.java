package com.grs.grs_client.model;

import lombok.Data;

@Data
public class OfficesGRO {
    private Long id;
    private Long officeId;
    private String officeNameBangla;
    private String officeNameEnglish;
    private Long groOfficeId;
    private Long groOfficeUnitOrganogramId;
    private Long appealOfficeId;
    private Long appealOfficerOfficeUnitOrganogramId;
    private Boolean isAppealOfficer;
    private Long adminOfficeId;
    private Long adminOfficeUnitOrganogramId;
    private String groOfficeUnitName;
    private String aoOfficeUnitName;
    private String adminOfficeUnitName;
    private Boolean status = false;
    private Integer layerLevel;
    private Integer customLayerLevel;
    private Integer customLayerId;
    private Long officeLayerId;
    private Long officeOriginId;
    private Long officeMinistryId;
}
