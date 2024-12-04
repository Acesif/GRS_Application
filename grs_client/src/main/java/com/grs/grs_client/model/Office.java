package com.grs.grs_client.model;

import lombok.Data;

@Data
public class Office {
    private Long id;
    private OfficeMinistry officeMinistry;
    private OfficeLayer officeLayer;
    private Long parentOfficeId;
    private String nameEnglish;
    private String nameBangla;
    private Integer districtId;
    private Integer divisionId;
    private Integer upazilaId;
    private String websiteUrl;
    private Long officeOriginId;
    private Boolean status;
    private Office parentOffice;
}
