package com.grs.grs_client.model;

import lombok.Data;

@Data
public class OfficeUnit {
    private Long id;
    private Office office;
    private OfficeMinistry officeMinistry;
    private OfficeLayer officeLayer;
    private OfficeUnit parentOfficeUnit;
    private String unitNameEnglish;
    private String unitNameBangla;
    private String email;
    private String phoneNumber;
}
