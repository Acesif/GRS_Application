package com.grs.grs_client.model;

import lombok.Data;

@Data
public class OfficeMinistry {
    private Long id;
    private Integer officeType;
    private String nameBangla;
    private String nameEnglish;
    private String nameEnglishShort;
    private String referenceCode;
    private Boolean status;
}
